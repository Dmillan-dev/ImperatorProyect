[CmdletBinding()]
param(
    [string]$ComposeFile = "infra/docker/compose.yaml",
    [string]$EnvFile = "infra/docker/.env"
)

$ErrorActionPreference = "Stop"

function Resolve-Docker {
    $command = Get-Command docker -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }

    $local = Join-Path $env:LOCALAPPDATA `
        "Programs/DockerDesktop/resources/bin/docker.exe"
    if (Test-Path -LiteralPath $local) {
        $env:PATH = "$(Split-Path -Parent $local);$env:PATH"
        return $local
    }

    throw "Docker CLI was not found."
}

function Invoke-Docker {
    param([Parameter(ValueFromRemainingArguments)] [string[]]$Arguments)

    & $script:Docker @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "Docker command failed with exit code $LASTEXITCODE."
    }
}

$script:Docker = Resolve-Docker
$expectedServices = @(
    "backend",
    "flyway-migrate",
    "flyway-validate",
    "frontend",
    "postgres",
    "postgres-permissions"
)
$expectedNetworks = @(
    "application-internal",
    "data-internal",
    "frontend-ingress",
    "provider-egress"
)

Invoke-Docker --version
Invoke-Docker compose version
Invoke-Docker compose --env-file $EnvFile -f $ComposeFile config --quiet

$services = & $script:Docker compose --env-file $EnvFile `
    -f $ComposeFile config --services
if ($LASTEXITCODE -ne 0) {
    throw "Unable to read Compose services."
}

$actualServices = @($services | Sort-Object)
$missing = @($expectedServices | Where-Object { $_ -notin $actualServices })
$unexpected = @($actualServices | Where-Object { $_ -notin $expectedServices })
if ($missing.Count -gt 0 -or $unexpected.Count -gt 0) {
    throw "Compose service inventory does not match D092."
}

$rendered = Invoke-Docker compose --env-file $EnvFile `
    -f $ComposeFile config --format json
$config = $rendered | ConvertFrom-Json
$actualNetworks = @($config.networks.PSObject.Properties.Name | Sort-Object)
$missingNetworks = @(
    $expectedNetworks | Where-Object { $_ -notin $actualNetworks }
)
$unexpectedNetworks = @(
    $actualNetworks | Where-Object { $_ -notin $expectedNetworks }
)
if ($missingNetworks.Count -gt 0 -or $unexpectedNetworks.Count -gt 0) {
    throw "Compose network inventory does not match the D092 Contract Fix."
}

$publishedServices = @(
    $config.services.PSObject.Properties |
        Where-Object {
            $null -ne $_.Value.ports -and @($_.Value.ports).Count -gt 0
        } |
        ForEach-Object { $_.Name }
)
if ($publishedServices.Count -ne 1 -or $publishedServices[0] -ne "frontend") {
    throw "Only the frontend may publish a host port."
}

$published = & $script:Docker compose --env-file $EnvFile `
    -f $ComposeFile port frontend 3000
if ($LASTEXITCODE -ne 0) {
    throw "Frontend port is not published."
}
if ($published -notmatch "^127\.0\.0\.1:\d+$") {
    throw "Frontend exposure is not loopback-only."
}

foreach ($service in @("postgres", "backend", "frontend")) {
    $containerId = & $script:Docker compose --env-file $EnvFile `
        -f $ComposeFile ps --quiet $service
    if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($containerId)) {
        throw "$service is not running."
    }

    $state = & $script:Docker inspect --format `
        "{{.State.Status}}|{{if .State.Health}}{{.State.Health.Status}}{{end}}|{{.HostConfig.ReadonlyRootfs}}|{{.Config.User}}" `
        $containerId
    if ($LASTEXITCODE -ne 0) {
        throw "Unable to inspect $service."
    }
    Write-Output "$service=$state"
}

$unauthorizedStatus = Invoke-WebRequest `
    -Uri "http://127.0.0.1:$($published.Split(':')[-1])/api/v1/decisions" `
    -SkipHttpErrorCheck `
    -TimeoutSec 10
if ($unauthorizedStatus.StatusCode -ne 401) {
    throw "Expected D087 401 through the frontend proxy."
}

Write-Output "Docker Production Runtime verification: PASS"
