[CmdletBinding()]
param(
    [string]$ComposeFile = "infra/docker/compose.yaml",
    [string]$EnvFile = "infra/docker/.env",
    [string]$EvidenceDirectory = "build/d096",
    [string]$DashboardScreenshot = "",
    [switch]$SkipBuild,
    [switch]$KeepRuntime
)

$ErrorActionPreference = "Stop"

function Resolve-Docker {
    $command = Get-Command docker -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }
    $local = Join-Path $env:LOCALAPPDATA "Programs/DockerDesktop/resources/bin/docker.exe"
    if (Test-Path -LiteralPath $local) {
        return $local
    }
    throw "Docker CLI was not found."
}

function Resolve-Maven {
    $command = Get-Command mvn -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }
    $cached = Get-ChildItem `
        (Join-Path $HOME ".m2/wrapper/dists/apache-maven-3.9.16") `
        -Filter "mvn*" -Recurse -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -in @("mvn", "mvn.cmd") } |
        Select-Object -First 1
    return $cached.FullName
}

function Invoke-Docker {
    param([string[]]$Arguments)

    $output = & $script:Docker @Arguments 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Docker command failed: docker $($Arguments -join ' ')"
    }
    return $output
}

function Get-ContainerId {
    param([string]$Service)

    $identifier = Invoke-Docker ($script:Compose + @("ps", "--quiet", $Service))
    if ([string]::IsNullOrWhiteSpace(($identifier -join ""))) {
        throw "$Service is not running."
    }
    return ($identifier | Select-Object -First 1).Trim()
}

function Get-BackendStatus {
    param(
        [string]$ContainerId,
        [int]$Port,
        [string]$Path
    )

    $probe = "exec 3<>/dev/tcp/127.0.0.1/$Port; " +
        "printf 'GET $Path HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n' >&3; " +
        "read -r protocol status rest <&3; printf '%s' `$status"
    $status = Invoke-Docker @("exec", $ContainerId, "bash", "-ec", $probe)
    return [int](($status -join "").Trim())
}

function Wait-Healthy {
    param(
        [string]$ContainerId,
        [int]$Attempts = 30
    )

    foreach ($attempt in 1..$Attempts) {
        $health = Invoke-Docker @(
            "inspect", "--format", "{{if .State.Health}}{{.State.Health.Status}}{{end}}",
            $ContainerId
        )
        if (($health -join "").Trim() -eq "healthy") {
            return
        }
        Start-Sleep -Seconds 2
    }
    throw "Container $ContainerId did not become healthy."
}

function Write-Evidence {
    param(
        [string]$Name,
        [object]$Value
    )

    $path = Join-Path $script:Evidence $Name
    $Value | Set-Content -LiteralPath $path -Encoding utf8
}

$script:Docker = Resolve-Docker
$repository = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$composePath = (Resolve-Path $ComposeFile).Path
$envPath = (Resolve-Path $EnvFile).Path
$script:Evidence = [IO.Path]::GetFullPath((Join-Path $repository $EvidenceDirectory))
$allowedEvidenceRoot = [IO.Path]::GetFullPath((Join-Path $repository "build/d096"))
if (-not $script:Evidence.StartsWith($allowedEvidenceRoot, [StringComparison]::OrdinalIgnoreCase)) {
    throw "D096 evidence must stay under build/d096."
}
New-Item -ItemType Directory -Force -Path $script:Evidence | Out-Null

$script:Compose = @(
    "compose", "--env-file", $envPath, "--file", $composePath,
    "--profile", "observability"
)

try {
    Write-Evidence "tool-versions.txt" @(
        "docker=$((Invoke-Docker @("--version")) -join '')"
        "compose=$((Invoke-Docker @("compose", "version")) -join '')"
    )

    Invoke-Docker ($script:Compose + @("config", "--quiet")) | Out-Null
    $rendered = (Invoke-Docker ($script:Compose + @("config", "--format", "json"))) -join "`n"
    $config = $rendered | ConvertFrom-Json
    $services = @($config.services.PSObject.Properties.Name | Sort-Object)
    $expectedServices = @(
        "backend", "flyway-migrate", "flyway-validate", "frontend", "grafana",
        "postgres", "postgres-permissions", "prometheus"
    ) | Sort-Object
    if (Compare-Object $expectedServices $services) {
        throw "Observability service inventory does not match D096."
    }

    $published = @(
        $config.services.PSObject.Properties |
            Where-Object { $null -ne $_.Value.ports } |
            ForEach-Object Name |
            Sort-Object
    )
    if (Compare-Object @("frontend", "grafana") $published) {
        throw "Only frontend and Grafana may publish host ports."
    }
    $grafanaBinding = @($config.services.grafana.ports)[0]
    if ($grafanaBinding.host_ip -ne "127.0.0.1") {
        throw "Grafana is not loopback-only."
    }

    $promtoolConfig = Invoke-Docker ($script:Compose + @(
        "run", "--rm", "--no-deps", "--entrypoint", "/bin/promtool",
        "prometheus", "check", "config", "/etc/prometheus/prometheus.yml"
    ))
    Write-Evidence "promtool-config.txt" $promtoolConfig
    $promtoolRules = Invoke-Docker ($script:Compose + @(
        "run", "--rm", "--no-deps", "--entrypoint", "/bin/promtool",
        "prometheus", "test", "rules",
        "/etc/prometheus/tests/imperator-alerts.test.yml"
    ))
    Write-Evidence "promtool-alert-transitions.txt" $promtoolRules

    $upArguments = @("up", "--detach", "--wait", "--wait-timeout", "300")
    if (-not $SkipBuild) {
        $upArguments += "--build"
    }
    Invoke-Docker ($script:Compose + $upArguments) | Out-Null

    $backend = Get-ContainerId "backend"
    $frontend = Get-ContainerId "frontend"
    $postgres = Get-ContainerId "postgres"
    $prometheus = Get-ContainerId "prometheus"
    $grafana = Get-ContainerId "grafana"

    $states = foreach ($service in @("backend", "frontend", "postgres", "prometheus", "grafana")) {
        $identifier = Get-ContainerId $service
        $state = Invoke-Docker @(
            "inspect", "--format",
            "{{.State.Status}}|{{if .State.Health}}{{.State.Health.Status}}{{end}}|{{.HostConfig.ReadonlyRootfs}}|{{.Config.User}}",
            $identifier
        )
        "$service=$(($state -join '').Trim())"
    }
    Write-Evidence "service-states.txt" $states

    $endpointMatrix = [ordered]@{
        livez = Get-BackendStatus $backend 8080 "/livez"
        readyz = Get-BackendStatus $backend 8080 "/readyz"
        health = Get-BackendStatus $backend 9090 "/actuator/health"
        liveness = Get-BackendStatus $backend 9090 "/actuator/health/liveness"
        readiness = Get-BackendStatus $backend 9090 "/actuator/health/readiness"
        prometheus = Get-BackendStatus $backend 9090 "/actuator/prometheus"
        env = Get-BackendStatus $backend 9090 "/actuator/env"
        configprops = Get-BackendStatus $backend 9090 "/actuator/configprops"
        beans = Get-BackendStatus $backend 9090 "/actuator/beans"
        mappings = Get-BackendStatus $backend 9090 "/actuator/mappings"
        loggers = Get-BackendStatus $backend 9090 "/actuator/loggers"
        heapdump = Get-BackendStatus $backend 9090 "/actuator/heapdump"
        threaddump = Get-BackendStatus $backend 9090 "/actuator/threaddump"
        shutdown = Get-BackendStatus $backend 9090 "/actuator/shutdown"
        caches = Get-BackendStatus $backend 9090 "/actuator/caches"
        conditions = Get-BackendStatus $backend 9090 "/actuator/conditions"
        sessions = Get-BackendStatus $backend 9090 "/actuator/sessions"
        scheduledtasks = Get-BackendStatus $backend 9090 "/actuator/scheduledtasks"
    }
    foreach ($required in @(
        "livez", "readyz", "health", "liveness", "readiness", "prometheus"
    )) {
        if ($endpointMatrix[$required] -ne 200) {
            throw "$required did not return HTTP 200."
        }
    }
    foreach ($forbidden in @(
        "env", "configprops", "beans", "mappings", "loggers", "heapdump",
        "threaddump", "shutdown", "caches", "conditions", "sessions",
        "scheduledtasks"
    )) {
        if ($endpointMatrix[$forbidden] -ne 404) {
            throw "$forbidden is unexpectedly accessible."
        }
    }
    Write-Evidence "endpoint-matrix.json" ($endpointMatrix | ConvertTo-Json)

    $frontendPort = (Invoke-Docker ($script:Compose + @("port", "frontend", "3000")) -join "").Trim()
    $grafanaPort = (Invoke-Docker ($script:Compose + @("port", "grafana", "3000")) -join "").Trim()
    if ($frontendPort -notmatch "^127\.0\.0\.1:\d+$" -or
        $grafanaPort -notmatch "^127\.0\.0\.1:\d+$") {
        throw "Frontend or Grafana is not bound to loopback."
    }
    $correlationId = [guid]::NewGuid().ToString()
    $sentinelToken = "d096-sentinel-token-never-log"
    $unauthorized = Invoke-WebRequest `
        -Uri "http://$frontendPort/api/v1/decisions" `
        -Headers @{
            Authorization = "Bearer $sentinelToken"
            "X-Correlation-ID" = $correlationId
        } `
        -SkipHttpErrorCheck `
        -TimeoutSec 10
    if ($unauthorized.StatusCode -ne 401 -or
        $unauthorized.Headers["X-Correlation-ID"] -ne $correlationId) {
        throw "Correlation or authentication behavior changed through the frontend."
    }

    Start-Sleep -Seconds 2
    $rawLogs = (Invoke-Docker @("logs", $backend)) -join "`n"
    if ($rawLogs.Contains($sentinelToken)) {
        throw "The sentinel authentication token reached application logs."
    }
    foreach ($secretName in @(
        "postgres-owner-password", "postgres-app-password", "grafana-admin-password"
    )) {
        $secretPath = Join-Path (Split-Path $composePath) "secrets/$secretName"
        if (Test-Path -LiteralPath $secretPath) {
            $secret = Get-Content -Raw -LiteralPath $secretPath
            if (-not [string]::IsNullOrWhiteSpace($secret) -and $rawLogs.Contains($secret)) {
                throw "A file-backed secret reached application logs."
            }
        }
    }

    $allowedFields = @(
        "@timestamp", "log", "process", "service", "message", "ecs", "event",
        "module", "correlation_id", "outcome", "duration_ms", "route_id",
        "source_type", "command", "actor_role", "error_code"
    )
    $events = @()
    foreach ($line in @($rawLogs -split "`r?`n" | Where-Object { $_ })) {
        try {
            $event = $line | ConvertFrom-Json
        } catch {
            throw "Backend emitted a non-JSON log line."
        }
        $unexpected = @($event.PSObject.Properties.Name | Where-Object { $_ -notin $allowedFields })
        if ($unexpected.Count -gt 0) {
            throw "Backend ECS log contains forbidden fields: $($unexpected -join ', ')."
        }
        if ($event.event) {
            $events += $event
        }
    }
    if (-not ($events | Where-Object correlation_id -eq $correlationId)) {
        throw "The normalized correlation ID did not reach structured logs."
    }
    $sampleEvents = $events | Select-Object -First 12 `
        "@timestamp", "event", "module", "outcome", "duration_ms", "route_id"
    Write-Evidence "sanitized-events.json" ($sampleEvents | ConvertTo-Json -Depth 4)

    $target = Invoke-Docker @(
        "exec", $prometheus, "/bin/promtool", "query", "instant",
        "http://127.0.0.1:9090", 'up{job="imperator-backend"}'
    )
    if (($target -join "`n") -notmatch "=> 1") {
        throw "Prometheus is not scraping the backend."
    }
    Write-Evidence "prometheus-target.txt" $target

    $metricNames = Invoke-Docker @(
        "exec", $prometheus, "/bin/promtool", "query", "series",
        '--match={__name__=~"imperator_.+"}', "http://127.0.0.1:9090"
    )
    if (@($metricNames).Count -gt 256) {
        throw "The runtime exposes more than 256 custom IMPERATOR series."
    }
    $metricText = $metricNames -join "`n"
    if ($metricText -notmatch "imperator_security_authentication_failure_total" -or
        $metricText -notmatch 'reason="(missing_token|invalid_token)"') {
        throw "The authoritative authentication failure metric is absent."
    }
    foreach ($forbiddenLabel in @(
        "correlation_id", "decision_id", "evidence_id", "ledger_id", "case_id",
        "actor", "subject", "email", "repository", "organization", "account",
        "region", "exception", "message", "path", "uri"
    )) {
        if ($metricText -match "(?m)[,{ ]$([regex]::Escape($forbiddenLabel))=") {
            throw "A forbidden custom metric label was exposed: $forbiddenLabel."
        }
    }
    Write-Evidence "custom-metric-series.txt" $metricNames

    $grafanaPasswordPath = Join-Path (Split-Path $composePath) "secrets/grafana-admin-password"
    $grafanaPassword = Get-Content -Raw -LiteralPath $grafanaPasswordPath
    $basic = [Convert]::ToBase64String(
        [Text.Encoding]::UTF8.GetBytes("admin:$grafanaPassword")
    )
    $headers = @{ Authorization = "Basic $basic" }
    $grafanaHostPort = $grafanaPort.Split(":")[-1]
    $dashboards = Invoke-RestMethod `
        -Uri "http://127.0.0.1:$grafanaHostPort/api/search?type=dash-db" `
        -Headers $headers -TimeoutSec 10
    $dashboard = Invoke-RestMethod `
        -Uri "http://127.0.0.1:$grafanaHostPort/api/dashboards/uid/imperator-operations" `
        -Headers $headers -TimeoutSec 10
    $rows = @($dashboard.dashboard.panels | Where-Object type -eq "row")
    if (@($dashboards).Count -ne 1 -or
        $dashboard.dashboard.title -ne "IMPERATOR Operations" -or
        $rows.Count -ne 5) {
        throw "Grafana dashboard provisioning does not match D096."
    }
    Write-Evidence "grafana-provisioning.json" ([ordered]@{
        dashboardCount = @($dashboards).Count
        title = $dashboard.dashboard.title
        uid = $dashboard.dashboard.uid
        rowCount = $rows.Count
        panelCount = @($dashboard.dashboard.panels).Count
    } | ConvertTo-Json)

    if ($DashboardScreenshot) {
        $screenshotPath = (Resolve-Path $DashboardScreenshot).Path
        Copy-Item -LiteralPath $screenshotPath `
            -Destination (Join-Path $script:Evidence "grafana-dashboard.png")
    }

    Invoke-Docker @("stop", $postgres) | Out-Null
    try {
        Start-Sleep -Seconds 3
        $databaseTransition = [ordered]@{
            livenessWhileDatabaseDown = Get-BackendStatus $backend 8080 "/livez"
            readinessWhileDatabaseDown = Get-BackendStatus $backend 8080 "/readyz"
        }
        if ($databaseTransition.livenessWhileDatabaseDown -ne 200 -or
            $databaseTransition.readinessWhileDatabaseDown -ne 503) {
            throw "Database loss did not preserve liveness and fail readiness."
        }
    } finally {
        Invoke-Docker @("start", $postgres) | Out-Null
        Wait-Healthy $postgres
    }
    foreach ($attempt in 1..30) {
        if ((Get-BackendStatus $backend 8080 "/readyz") -eq 200) {
            break
        }
        Start-Sleep -Seconds 2
    }
    $databaseTransition.readinessAfterRecovery = Get-BackendStatus $backend 8080 "/readyz"
    if ($databaseTransition.readinessAfterRecovery -ne 200) {
        throw "Backend readiness did not recover after PostgreSQL restart."
    }
    Write-Evidence "readiness-transition.json" ($databaseTransition | ConvertTo-Json)

    Invoke-Docker @("stop", $grafana, $prometheus) | Out-Null
    try {
        $isolation = [ordered]@{
            readyz = Get-BackendStatus $backend 8080 "/readyz"
            frontend = (Invoke-WebRequest -Uri "http://$frontendPort/" -TimeoutSec 10).StatusCode
            readRoute = (Invoke-WebRequest `
                -Uri "http://$frontendPort/api/v1/decisions" `
                -SkipHttpErrorCheck -TimeoutSec 10).StatusCode
            commandRoute = (Invoke-WebRequest `
                -Uri "http://$frontendPort/api/v1/decisions" `
                -Method Post -ContentType "application/json" -Body "{}" `
                -SkipHttpErrorCheck -TimeoutSec 10).StatusCode
        }
        if ($isolation.readyz -ne 200 -or $isolation.frontend -ne 200 -or
            $isolation.readRoute -ne 401 -or $isolation.commandRoute -ne 401) {
            throw "Observability outage changed product availability."
        }
        Write-Evidence "observability-outage-isolation.json" ($isolation | ConvertTo-Json)
    } finally {
        Invoke-Docker @("start", $prometheus, $grafana) | Out-Null
        Wait-Healthy $prometheus
        Wait-Healthy $grafana
    }

    $maven = Resolve-Maven
    if ($maven) {
        & $maven "-q" "dependency:tree" `
            "-DoutputFile=$($script:Evidence)/dependency-tree.txt"
        if ($LASTEXITCODE -ne 0) {
            throw "Unable to record the managed dependency tree."
        }
    }

    $images = foreach ($service in @("prometheus", "grafana")) {
        $identifier = Get-ContainerId $service
        $identity = Invoke-Docker @(
            "inspect", "--format", "{{.Config.Image}}|{{.Image}}", $identifier
        )
        "$service=$(($identity -join '').Trim())"
    }
    $manifest = [ordered]@{
        sourceRevision = (& git -C $repository rev-parse HEAD).Trim()
        worktreeDirty = [bool](& git -C $repository status --porcelain)
        generatedAt = [DateTimeOffset]::UtcNow.ToString("O")
        images = $images
        screenshotIncluded = [bool]$DashboardScreenshot
        result = if ($DashboardScreenshot) {
            "PASS"
        } else {
            "PASS_WITH_SCREENSHOT_PENDING"
        }
    }
    Write-Evidence "manifest.json" ($manifest | ConvertTo-Json -Depth 4)
    Write-Output "D096 Observability Runtime verification: $($manifest.result)"
} finally {
    if (-not $KeepRuntime) {
        Invoke-Docker ($script:Compose + @("down", "--remove-orphans")) | Out-Null
    }
}
