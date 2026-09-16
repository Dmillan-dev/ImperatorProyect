[CmdletBinding()]
param(
    [string]$ComposeFile = "infra/docker/compose.yaml",
    [string]$EnvFile = "infra/docker/.env",
    [string]$ImageTag = "d097-local",
    [string]$EvidenceDirectory = "build/d097",
    [switch]$KeepRuntime
)

$ErrorActionPreference = "Stop"

function Invoke-Docker {
    param(
        [Parameter(Mandatory)]
        [string[]]$Arguments
    )

    & $script:Docker @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "docker $($Arguments -join ' ') failed with exit code $LASTEXITCODE."
    }
}

function Get-DockerOutput {
    param(
        [Parameter(Mandatory)]
        [string[]]$Arguments
    )

    $output = & $script:Docker @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "docker $($Arguments -join ' ') failed with exit code $LASTEXITCODE."
    }
    return @($output)
}

function Write-Evidence {
    param(
        [Parameter(Mandatory)]
        [string]$Name,
        [Parameter(Mandatory)]
        [string]$Content
    )

    [System.IO.File]::WriteAllText(
        (Join-Path $script:EvidenceRoot $Name),
        $Content,
        [System.Text.UTF8Encoding]::new($false)
    )
}

function Get-ContainerId {
    param([Parameter(Mandatory)][string]$Service)

    $id = (@(Get-DockerOutput -Arguments (
        $script:Compose + @("ps", "--quiet", $Service)
    )) -join "").Trim()
    if ($id -notmatch "^[0-9a-f]{12,64}$") {
        throw "Compose service $Service does not have one container."
    }
    return $id
}

function Get-InternalResponse {
    param(
        [Parameter(Mandatory)][string]$Container,
        [Parameter(Mandatory)][int]$Port,
        [Parameter(Mandatory)][string]$Path,
        [string[]]$Headers = @()
    )

    $headerText = ""
    foreach ($header in $Headers) {
        $headerText += "$header`r`n"
    }
    $request = "GET $Path HTTP/1.1`r`nHost: localhost`r`n$headerText" + "Connection: close`r`n`r`n"
    $command = "exec 3<>/dev/tcp/127.0.0.1/$Port; printf '%b' '$request' >&3; cat <&3"
    return (@(Get-DockerOutput -Arguments @(
        "exec", "--user", "10001:10001", $Container,
        "timeout", "12", "bash", "-ec", $command
    )) -join "`n")
}

function Wait-HttpStatus {
    param(
        [Parameter(Mandatory)][string]$Container,
        [Parameter(Mandatory)][int]$Port,
        [Parameter(Mandatory)][string]$Path,
        [Parameter(Mandatory)][int]$ExpectedStatus,
        [string[]]$Headers = @(),
        [int]$Attempts = 20
    )

    for ($attempt = 1; $attempt -le $Attempts; $attempt++) {
        try {
            $response = Get-InternalResponse -Container $Container -Port $Port -Path $Path -Headers $Headers
            if ($response -match "^HTTP/1\.[01] $ExpectedStatus ") {
                return $response
            }
        } catch {
            if ($attempt -eq $Attempts) {
                throw
            }
        }
        Start-Sleep -Seconds 2
    }
    throw "$Path did not return HTTP $ExpectedStatus after $Attempts attempts."
}

function Wait-ContainerHealthy {
    param(
        [Parameter(Mandatory)][string]$Container,
        [int]$Attempts = 30
    )

    for ($attempt = 1; $attempt -le $Attempts; $attempt++) {
        $status = (@(Get-DockerOutput -Arguments @(
            "inspect", "--format", "{{.State.Health.Status}}", $Container
        )) -join "").Trim()
        if ($status -eq "healthy") {
            return
        }
        Start-Sleep -Seconds 2
    }
    throw "Container $Container did not become healthy."
}

$repositoryRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRoot

$script:Docker = (Get-Command docker -ErrorAction Stop).Source
$git = (Get-Command git -ErrorAction Stop).Source
$composePath = [System.IO.Path]::GetFullPath((Join-Path $repositoryRoot $ComposeFile))
$envPath = [System.IO.Path]::GetFullPath((Join-Path $repositoryRoot $EnvFile))
if (-not (Test-Path -LiteralPath $composePath)) {
    throw "Compose file does not exist: $composePath"
}
if (-not (Test-Path -LiteralPath $envPath)) {
    throw "Ignored local Compose environment does not exist: $envPath"
}

$script:EvidenceRoot = [System.IO.Path]::GetFullPath(
    (Join-Path $repositoryRoot $EvidenceDirectory)
)
$allowedEvidenceRoot = [System.IO.Path]::GetFullPath(
    (Join-Path $repositoryRoot "build")
)
$allowedPrefix = $allowedEvidenceRoot.TrimEnd(
    [System.IO.Path]::DirectorySeparatorChar,
    [System.IO.Path]::AltDirectorySeparatorChar
) + [System.IO.Path]::DirectorySeparatorChar
if (-not $script:EvidenceRoot.StartsWith(
        $allowedPrefix,
        [System.StringComparison]::OrdinalIgnoreCase
    )) {
    throw "D097 evidence must remain below the ignored build directory."
}
New-Item -ItemType Directory -Force -Path $script:EvidenceRoot | Out-Null

$deferredDirectory = Join-Path $repositoryRoot "infra/docker/observability"
if (Test-Path -LiteralPath $deferredDirectory) {
    $remainingAssets = @(Get-ChildItem -LiteralPath $deferredDirectory -Recurse -File)
    if ($remainingAssets.Count -gt 0) {
        throw "D097 forbids external monitoring assets below $deferredDirectory."
    }
}
$runtimeInputs = @(
    $composePath,
    (Join-Path $repositoryRoot "infra/docker/.env.example"),
    (Join-Path $repositoryRoot "infra/docker/secrets/README.md")
)
$forbiddenPatterns = [ordered]@{
    monitoringService = "(?m)^\s{2}(prometheus|grafana|alertmanager):\s*$"
    monitoringProfile = "(?m)^\s+-\s+observability\s*$"
    monitoringNetwork = "(?m)^\s{2}observability-internal:\s*$"
    monitoringVolume = "(?m)^\s{2}prometheus-data:\s*$"
    grafanaPort = "IMPERATOR_" + "GRAFANA_PORT"
    grafanaSecret = "grafana-" + "admin-password"
}
foreach ($path in $runtimeInputs) {
    $content = Get-Content -Raw -LiteralPath $path
    foreach ($entry in $forbiddenPatterns.GetEnumerator()) {
        if ($content -match $entry.Value) {
            throw "D097 found forbidden $($entry.Key) in $path."
        }
    }
}

$script:Compose = @(
    "compose", "--env-file", $envPath, "-f", $composePath
)
$previousImageTag = $env:IMPERATOR_IMAGE_TAG
$postgresStopped = $false
try {
    $env:IMPERATOR_IMAGE_TAG = $ImageTag
    Invoke-Docker -Arguments @("--version")
    Invoke-Docker -Arguments @("compose", "version")

    $configJson = @(Get-DockerOutput -Arguments (
        $script:Compose + @("config", "--format", "json")
    )) -join "`n"
    $config = $configJson | ConvertFrom-Json
    $services = @($config.services.PSObject.Properties.Name | Sort-Object)
    $expectedServices = @(
        "backend",
        "flyway-migrate",
        "flyway-validate",
        "frontend",
        "postgres",
        "postgres-permissions"
    )
    if (Compare-Object $expectedServices $services) {
        throw "D097 Compose service inventory does not match the bounded runtime."
    }

    $backend = $config.services.backend
    if (@($backend.ports | Where-Object { $null -ne $_ }).Count -ne 0) {
        throw "Backend ports must remain unpublished."
    }
    $backendExposure = @($backend.expose | ForEach-Object { [string]$_ })
    if (-not ($backendExposure -match "^9090(/tcp)?$")) {
        throw "Backend management port 9090 is not exposed internally."
    }

    Invoke-Docker -Arguments (
        $script:Compose + @("up", "--detach", "--wait", "--build")
    )

    $backendId = Get-ContainerId -Service "backend"
    $postgresId = Get-ContainerId -Service "postgres"

    $portBindingsJson = (@(Get-DockerOutput -Arguments @(
        "inspect", "--format", "{{json .HostConfig.PortBindings}}", $backendId
    )) -join "").Trim()
    $portBindings = $portBindingsJson | ConvertFrom-Json
    if (@($portBindings.PSObject.Properties).Count -ne 0) {
        throw "Backend ports are published to the host."
    }

    $endpointMatrix = [ordered]@{}
    foreach ($probe in @(
        @{ Name = "livez"; Port = 8080; Path = "/livez"; Status = 200 },
        @{ Name = "readyz"; Port = 8080; Path = "/readyz"; Status = 200 },
        @{ Name = "liveness"; Port = 9090; Path = "/actuator/health/liveness"; Status = 200 },
        @{ Name = "readiness"; Port = 9090; Path = "/actuator/health/readiness"; Status = 200 },
        @{ Name = "metrics"; Port = 9090; Path = "/actuator/prometheus"; Status = 200 }
    )) {
        $response = Wait-HttpStatus -Container $backendId -Port $probe.Port -Path $probe.Path -ExpectedStatus $probe.Status
        $endpointMatrix[$probe.Name] = $probe.Status
        if ($probe.Name -ne "metrics" -and $response -match "details|components") {
            throw "$($probe.Path) exposed health details or components."
        }
    }

    foreach ($endpoint in @(
        "env", "configprops", "beans", "mappings", "loggers", "heapdump",
        "threaddump", "shutdown", "caches", "conditions", "sessions",
        "scheduledtasks"
    )) {
        Wait-HttpStatus -Container $backendId -Port 9090 -Path "/actuator/$endpoint" -ExpectedStatus 404 | Out-Null
    }

    $correlationId = "4f82af53-b862-4c03-b4d8-78d85cc67d67"
    $sentinel = "sentinel-d097-secret-material"
    Wait-HttpStatus -Container $backendId -Port 8080 -Path "/api/v1/decisions" -ExpectedStatus 401 -Headers @(
        "X-Correlation-ID: $correlationId",
        "Authorization: Bearer $sentinel"
    ) | Out-Null

    $metricsResponse = Wait-HttpStatus -Container $backendId -Port 9090 -Path "/actuator/prometheus" -ExpectedStatus 200
    foreach ($requiredMetric in @(
        "jvm_memory_used_bytes",
        "process_uptime_seconds",
        "http_server_requests",
        "imperator_security_authentication_failure_total"
    )) {
        if (-not $metricsResponse.Contains($requiredMetric)) {
            throw "Required metric is absent: $requiredMetric"
        }
    }
    foreach ($forbiddenMetricValue in @($correlationId, $sentinel, "exception=")) {
        if ($metricsResponse.Contains($forbiddenMetricValue)) {
            throw "Metrics contain forbidden request-controlled data."
        }
    }
    $customSeries = @(
        $metricsResponse -split "\n" |
            Where-Object {
                $_ -match "^imperator_[a-z0-9_]+(\{|\s)" -and
                $_ -notmatch "^#"
            }
    )
    if ($customSeries.Count -gt 256) {
        throw "D097 custom metric series exceed the bounded fixture limit."
    }

    Invoke-Docker -Arguments @("stop", $postgresId)
    $postgresStopped = $true
    Wait-HttpStatus -Container $backendId -Port 8080 -Path "/livez" -ExpectedStatus 200 | Out-Null
    Wait-HttpStatus -Container $backendId -Port 8080 -Path "/readyz" -ExpectedStatus 503 | Out-Null
    Wait-HttpStatus -Container $backendId -Port 9090 -Path "/actuator/health/readiness" -ExpectedStatus 503 | Out-Null
    $endpointMatrix["postgres_down_liveness"] = 200
    $endpointMatrix["postgres_down_readiness"] = 503

    Invoke-Docker -Arguments @("start", $postgresId)
    $postgresStopped = $false
    Wait-ContainerHealthy -Container $postgresId
    Wait-HttpStatus -Container $backendId -Port 8080 -Path "/readyz" -ExpectedStatus 200 | Out-Null
    $endpointMatrix["postgres_recovered_readiness"] = 200

    $logLines = @(& $script:Docker logs $backendId 2>&1)
    if ($LASTEXITCODE -ne 0) {
        throw "Unable to read backend logs."
    }
    $applicationJsonLines = @(
        $logLines |
            ForEach-Object { [string]$_ } |
            Where-Object { $_.TrimStart().StartsWith("{") }
    )
    if ($applicationJsonLines.Count -eq 0) {
        throw "No structured application JSON events were emitted."
    }
    foreach ($line in $applicationJsonLines) {
        $null = $line | ConvertFrom-Json
    }
    $joinedLogs = $logLines -join "`n"
    if ($joinedLogs.Contains($sentinel)) {
        throw "Application logs contain request-controlled secret material."
    }
    if (-not $joinedLogs.Contains($correlationId)) {
        throw "Structured logs did not retain the canonical correlation ID."
    }
    if (-not $joinedLogs.Contains("http.request.completed") -or
        -not $joinedLogs.Contains("authentication.failed")) {
        throw "Required safe application events are absent."
    }

    $revision = (@(& $git rev-parse HEAD) -join "").Trim()
    if ($LASTEXITCODE -ne 0 -or $revision -notmatch "^[0-9a-f]{40}$") {
        throw "Unable to resolve the source revision."
    }
    $worktreeStatus = @(& $git status --porcelain=v1)
    if ($LASTEXITCODE -ne 0) {
        throw "Unable to resolve the worktree state."
    }
    Write-Evidence -Name "endpoint-matrix.json" -Content (
        $endpointMatrix | ConvertTo-Json
    )
    Write-Evidence -Name "metric-names.txt" -Content (
        (($metricsResponse -split "\n" |
            Where-Object { $_ -match "^# (HELP|TYPE) " } |
            ForEach-Object { ($_ -split "\s+")[2] } |
            Sort-Object -Unique) -join "`n") + "`n"
    )
    Write-Evidence -Name "safe-event-sample.jsonl" -Content (
        (($applicationJsonLines |
            Where-Object {
                $_.Contains("http.request.completed") -or
                $_.Contains("authentication.failed")
            } |
            Select-Object -Last 4) -join "`n") + "`n"
    )
    $manifest = [ordered]@{
        decision = "D097"
        sourceRevision = $revision
        worktreeDirty = $worktreeStatus.Count -gt 0
        imageTag = $ImageTag
        services = $services
        managementPortPublished = $false
        deferredMonitoringAssets = 0
        customMetricSeries = $customSeries.Count
        endpoints = $endpointMatrix
        result = "PASS"
    }
    Write-Evidence -Name "verification.json" -Content (
        $manifest | ConvertTo-Json -Depth 6
    )

    Write-Output "D097 application-native observability verification: PASS"
    Write-Output "Evidence=$script:EvidenceRoot"
} finally {
    if ($postgresStopped) {
        try {
            Invoke-Docker -Arguments @("start", $postgresId)
        } catch {
            Write-Warning "PostgreSQL could not be restarted during cleanup."
        }
    }
    if (-not $KeepRuntime) {
        try {
            Invoke-Docker -Arguments ($script:Compose + @("down", "--remove-orphans"))
        } catch {
            Write-Warning "Compose cleanup did not complete."
        }
    }
    $env:IMPERATOR_IMAGE_TAG = $previousImageTag
}
