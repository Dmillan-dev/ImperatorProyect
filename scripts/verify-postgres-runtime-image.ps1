[CmdletBinding()]
param(
    [string]$ImageTag,
    [string]$EvidenceDirectory = "build/d094",
    [string]$ExpectedRuntimeDigest,
    [switch]$RequireCleanWorktree
)

$ErrorActionPreference = "Stop"

if (-not [string]::IsNullOrWhiteSpace($ExpectedRuntimeDigest) -and
    $ExpectedRuntimeDigest -notmatch "^sha256:[0-9a-f]{64}$") {
    throw "ExpectedRuntimeDigest must be a lowercase SHA-256 digest."
}

function Invoke-Checked {
    param(
        [Parameter(Mandatory)] [string]$Command,
        [Parameter(ValueFromRemainingArguments)] [string[]]$Arguments
    )

    & $Command @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "$Command failed with exit code $LASTEXITCODE."
    }
}

function Get-CheckedOutput {
    param(
        [Parameter(Mandatory)] [string]$Command,
        [Parameter(ValueFromRemainingArguments)] [string[]]$Arguments
    )

    $output = & $Command @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "$Command failed with exit code $LASTEXITCODE."
    }
    return @($output)
}

function Write-Utf8NoBom {
    param(
        [Parameter(Mandatory)] [string]$Path,
        [Parameter(Mandatory)] [string]$Content
    )

    [System.IO.File]::WriteAllText(
        $Path,
        $Content,
        [System.Text.UTF8Encoding]::new($false)
    )
}

$repositoryRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repositoryRoot

$docker = (Get-Command docker -ErrorAction Stop).Source
$trivy = (Get-Command trivy -ErrorAction Stop).Source
$git = (Get-Command git -ErrorAction Stop).Source
$tar = (Get-Command tar -ErrorAction Stop).Source

$revision = (@(Get-CheckedOutput $git rev-parse HEAD))[0].Trim()
if ($revision -notmatch "^[0-9a-f]{40}$") {
    throw "A full Git revision is required."
}
$sourceDateEpoch = (@(Get-CheckedOutput -Command $git -Arguments @(
    "show", "-s", "--format=%ct", $revision
)))[0].Trim()
if ($sourceDateEpoch -notmatch "^[0-9]{10,}$") {
    throw "A valid Git commit timestamp is required for reproducible output."
}
if ([string]::IsNullOrWhiteSpace($ImageTag)) {
    $ImageTag = "imperator/postgres:$revision"
}

$worktreeStatus = @(Get-CheckedOutput $git status --porcelain=v1)
$dirtyWorktree = $worktreeStatus.Count -gt 0
if ($RequireCleanWorktree -and $dirtyWorktree) {
    throw "Formal D094 certification requires a clean worktree."
}

$dockerfilePath = Join-Path $repositoryRoot "infra/docker/postgresql/Dockerfile"
$lockPath = Join-Path $repositoryRoot "infra/docker/postgresql/supply-chain-lock.json"
$contextPath = Join-Path $repositoryRoot "infra/docker/postgresql"
$lock = Get-Content -Raw -LiteralPath $lockPath | ConvertFrom-Json
$dockerfile = Get-Content -Raw -LiteralPath $dockerfilePath

$requiredFragments = @(
    "# syntax=$($lock.dockerfileFrontend.image)@$($lock.dockerfileFrontend.digest)",
    "FROM $($lock.builder.image)@$($lock.builder.digest)",
    "FROM $($lock.postgresql.image)@$($lock.postgresql.digest)",
    $lock.gosu.commit,
    $lock.gosu.sourceSha256,
    "CGO_ENABLED=0 GOOS=$($lock.build.goos) GOARCH=$($lock.build.goarch)",
    "-trimpath",
    "-buildvcs=false",
    "-buildid="
)
foreach ($fragment in $requiredFragments) {
    if (-not $dockerfile.Contains($fragment)) {
        throw "Dockerfile and supply-chain lock disagree: $fragment"
    }
}

$evidenceRoot = if ([System.IO.Path]::IsPathRooted($EvidenceDirectory)) {
    [System.IO.Path]::GetFullPath($EvidenceDirectory)
} else {
    [System.IO.Path]::GetFullPath(
        (Join-Path $repositoryRoot $EvidenceDirectory)
    )
}
$allowedEvidenceRoot = [System.IO.Path]::GetFullPath((Join-Path $repositoryRoot "build"))
$pathSeparators = [char[]]@(
    [System.IO.Path]::DirectorySeparatorChar,
    [System.IO.Path]::AltDirectorySeparatorChar
)
$allowedEvidencePrefix = $allowedEvidenceRoot.TrimEnd($pathSeparators) +
    [System.IO.Path]::DirectorySeparatorChar
if (-not $evidenceRoot.StartsWith(
        $allowedEvidencePrefix,
        [System.StringComparison]::OrdinalIgnoreCase
    )) {
    throw "D094 evidence must remain below the ignored build directory."
}
New-Item -ItemType Directory -Force -Path $evidenceRoot | Out-Null

$buildMetadataPath = Join-Path $evidenceRoot "buildkit-metadata.json"
$sbomPath = Join-Path $evidenceRoot "postgres-sbom.cdx.json"
$scanJsonPath = Join-Path $evidenceRoot "trivy-result.json"
$scanTablePath = Join-Path $evidenceRoot "trivy-report.txt"
$provenancePath = Join-Path $evidenceRoot "provenance.json"
$manifestPath = Join-Path $evidenceRoot "sha256-manifest.txt"
$ociArchivePath = Join-Path $evidenceRoot "postgres-image.oci.tar"

Invoke-Checked -Command $docker -Arguments @("--version")
Invoke-Checked -Command $docker -Arguments @("compose", "version")
Invoke-Checked -Command $docker -Arguments @(
    "info",
    "--format",
    "DockerServer={{.ServerVersion}} OS={{.OperatingSystem}} Architecture={{.Architecture}}"
)
Invoke-Checked -Command $docker -Arguments @("buildx", "version")
Invoke-Checked -Command $trivy -Arguments @("--version")

$buildArguments = @(
    "buildx", "build",
    "--platform", "linux/amd64",
    "--file", $dockerfilePath,
    "--tag", $ImageTag,
    "--build-arg", "VCS_REF=$revision",
    "--build-arg", "SOURCE_DATE_EPOCH=$sourceDateEpoch",
    "--metadata-file", $buildMetadataPath,
    "--provenance=mode=max",
    "--pull",
    "--no-cache",
    "--output", "type=oci,dest=$ociArchivePath,rewrite-timestamp=true"
)
$buildArguments += $contextPath
$previousSourceDateEpoch = $env:SOURCE_DATE_EPOCH
$runtimeManifestDigest = $null
try {
    $env:SOURCE_DATE_EPOCH = $sourceDateEpoch
    Invoke-Checked -Command $docker -Arguments $buildArguments

    $ociIndexJson = (Get-CheckedOutput -Command $tar -Arguments @(
        "-xOf", $ociArchivePath, "index.json"
    )) -join "`n"
    $ociIndex = $ociIndexJson | ConvertFrom-Json
    $imageIndexDescriptors = @($ociIndex.manifests)
    if ($imageIndexDescriptors.Count -ne 1) {
        throw "The OCI archive must contain exactly one image index."
    }
    $imageIndexDigest = [string]$imageIndexDescriptors[0].digest
    if ($imageIndexDigest -notmatch "^sha256:[0-9a-f]{64}$") {
        throw "The OCI image index is not content-addressed."
    }
    $imageIndexBlob = "blobs/sha256/" + $imageIndexDigest.Substring(7)
    $manifestListJson = (Get-CheckedOutput -Command $tar -Arguments @(
        "-xOf", $ociArchivePath, $imageIndexBlob
    )) -join "`n"
    $manifestList = $manifestListJson | ConvertFrom-Json
    $runtimeDescriptors = @(
        $manifestList.manifests | Where-Object {
            $_.mediaType -eq "application/vnd.oci.image.manifest.v1+json" -and
            $_.annotations.'vnd.docker.reference.type' -ne "attestation-manifest"
        }
    )
    if ($runtimeDescriptors.Count -ne 1) {
        throw "The OCI archive must contain exactly one linux/amd64 runtime manifest."
    }
    $runtimeManifestDigest = [string]$runtimeDescriptors[0].digest
    if ($runtimeManifestDigest -notmatch "^sha256:[0-9a-f]{64}$") {
        throw "The OCI runtime manifest is not content-addressed."
    }
    if ($runtimeDescriptors[0].platform.os -ne "linux" -or
        $runtimeDescriptors[0].platform.architecture -ne "amd64") {
        throw "The OCI runtime manifest is not linux/amd64."
    }
    if (-not [string]::IsNullOrWhiteSpace($ExpectedRuntimeDigest) -and
        $runtimeManifestDigest -ne $ExpectedRuntimeDigest) {
        throw "Reproducibility failed: expected $ExpectedRuntimeDigest but built $runtimeManifestDigest."
    }

    Invoke-Checked -Command $docker -Arguments @(
        "load", "--input", $ociArchivePath
    )
} finally {
    $env:SOURCE_DATE_EPOCH = $previousSourceDateEpoch
    Remove-Item -LiteralPath $ociArchivePath -Force -ErrorAction SilentlyContinue
}

$inspectJson = (Get-CheckedOutput -Command $docker -Arguments @(
    "image", "inspect", $ImageTag
)) -join "`n"
$inspect = $inspectJson | ConvertFrom-Json
$image = @($inspect)[0]
$imageId = [string]$image.Id
if ($imageId -notmatch "^sha256:[0-9a-f]{64}$") {
    throw "The final image does not have a content-addressed image ID."
}
if ($imageId -ne $imageIndexDigest) {
    throw "The loaded image ID does not match the exported OCI image index."
}
if ($image.Os -ne "linux" -or $image.Architecture -ne "amd64") {
    throw "The final image is not linux/amd64."
}
if (@($image.Config.Entrypoint).Count -ne 1 -or
    $image.Config.Entrypoint[0] -ne "docker-entrypoint.sh") {
    throw "The upstream PostgreSQL entrypoint changed."
}
if (@($image.Config.Cmd).Count -ne 1 -or $image.Config.Cmd[0] -ne "postgres") {
    throw "The upstream PostgreSQL default command changed."
}
if ($image.Config.User -ne
    "$($lock.acceptance.postgresUid):$($lock.acceptance.postgresGid)") {
    throw "The final image does not declare the PostgreSQL runtime UID/GID."
}

$buildMetadata = Get-Content -Raw -LiteralPath $buildMetadataPath |
    ConvertFrom-Json
if ($buildMetadata.'containerimage.digest' -ne $imageId) {
    throw "BuildKit metadata and the loaded OCI index disagree."
}

$labels = $image.Config.Labels
if ($labels.'org.opencontainers.image.revision' -ne $revision -or
    $labels.'org.opencontainers.image.base.digest' -ne
        $lock.postgresql.digest -or
    $labels.'org.opencontainers.image.version' -ne
        "18.6-gosu1.19-go1.26.6") {
    throw "The final OCI labels do not match the D094 inputs."
}

$gosuVersion = (Get-CheckedOutput -Command $docker -Arguments @(
    "run", "--rm",
    "--user", "0:0",
    "--entrypoint", "/usr/local/bin/gosu",
    $ImageTag,
    "--version"
)) -join "`n"
if ($gosuVersion -notmatch "^1\.19 \(" -or
    $gosuVersion -notmatch "go1\.26\.6") {
    throw "Unexpected gosu runtime identity: $gosuVersion"
}
Invoke-Checked -Command $docker -Arguments @(
    "run", "--rm",
    "--user", "0:0",
    "--entrypoint", "/usr/local/bin/gosu",
    $ImageTag,
    "nobody", "true"
)

$postgresVersion = (Get-CheckedOutput -Command $docker -Arguments @(
    "run", "--rm",
    "--entrypoint", "postgres",
    $ImageTag,
    "--version"
)) -join "`n"
if ($postgresVersion -notmatch "postgres \(PostgreSQL\) 18\.6") {
    throw "Unexpected PostgreSQL runtime identity: $postgresVersion"
}

Invoke-Checked -Command $trivy -Arguments @(
    "image",
    "--image-src", "docker",
    "--scanners", "vuln,secret",
    "--severity", "HIGH,CRITICAL",
    "--ignore-unfixed",
    "--format", "json",
    "--output", $scanJsonPath,
    $ImageTag
)

Invoke-Checked -Command $trivy -Arguments @(
    "image",
    "--image-src", "docker",
    "--scanners", "vuln,secret",
    "--severity", "HIGH,CRITICAL",
    "--ignore-unfixed",
    "--exit-code", "1",
    "--format", "table",
    "--output", $scanTablePath,
    $ImageTag
)

$scan = Get-Content -Raw -LiteralPath $scanJsonPath | ConvertFrom-Json
$vulnerabilities = @(
    $scan.Results |
        ForEach-Object { @($_.Vulnerabilities) } |
        Where-Object { $null -ne $_ }
)
$secrets = @(
    $scan.Results |
        ForEach-Object { @($_.Secrets) } |
        Where-Object { $null -ne $_ }
)
if ($vulnerabilities.Count -gt 0 -or $secrets.Count -gt 0) {
    throw "The final image has forbidden vulnerability or secret findings."
}
if ((Get-Content -Raw -LiteralPath $scanJsonPath).Contains(
        $lock.acceptance.forbiddenCve
    )) {
    throw "$($lock.acceptance.forbiddenCve) remains in the final scan."
}

Invoke-Checked -Command $trivy -Arguments @(
    "image",
    "--image-src", "docker",
    "--scanners", "vuln",
    "--format", "cyclonedx",
    "--output", $sbomPath,
    $ImageTag
)

$sbomContent = Get-Content -Raw -LiteralPath $sbomPath
if (-not $sbomContent.Contains($imageId)) {
    throw "The CycloneDX SBOM is not bound to the final image ID."
}

$trivyVersion = (Get-CheckedOutput -Command $trivy -Arguments @("--version")) -join "`n"
$dockerVersion = (Get-CheckedOutput -Command $docker -Arguments @(
    "version",
    "--format",
    "Client={{.Client.Version}} Server={{.Server.Version}}"
)) -join "`n"
$buildxVersion = (Get-CheckedOutput -Command $docker -Arguments @(
    "buildx", "version"
)) -join "`n"

$provenance = [ordered]@{
    schemaVersion = 1
    decision = "D094"
    sourceRevision = $revision
    dirtyWorktree = $dirtyWorktree
    reproducibilityVerified = -not [string]::IsNullOrWhiteSpace($ExpectedRuntimeDigest)
    formalReleaseEligible = (-not $dirtyWorktree) -and
        (-not [string]::IsNullOrWhiteSpace($ExpectedRuntimeDigest))
    target = "linux/amd64"
    sourceDateEpoch = $sourceDateEpoch
    imageReference = $ImageTag
    imageIndexDigest = $imageId
    runtimeManifestDigest = $runtimeManifestDigest
    postgresqlVersion = $postgresVersion.Trim()
    gosuVersion = $gosuVersion.Trim()
    dockerVersion = $dockerVersion.Trim()
    buildxVersion = $buildxVersion.Trim()
    trivyVersion = $trivyVersion.Trim()
    dockerfileSha256 = (
        Get-FileHash -Algorithm SHA256 -LiteralPath $dockerfilePath
    ).Hash.ToLowerInvariant()
    lockSha256 = (
        Get-FileHash -Algorithm SHA256 -LiteralPath $lockPath
    ).Hash.ToLowerInvariant()
    inputs = $lock
    evidence = [ordered]@{
        buildkitMetadata = [System.IO.Path]::GetFileName($buildMetadataPath)
        sbom = [System.IO.Path]::GetFileName($sbomPath)
        trivyJson = [System.IO.Path]::GetFileName($scanJsonPath)
        trivyTable = [System.IO.Path]::GetFileName($scanTablePath)
    }
}
Write-Utf8NoBom $provenancePath ($provenance | ConvertTo-Json -Depth 12)

$evidenceFiles = @(
    $buildMetadataPath,
    $sbomPath,
    $scanJsonPath,
    $scanTablePath,
    $provenancePath
)
$manifestLines = foreach ($path in $evidenceFiles) {
    $hash = (Get-FileHash -Algorithm SHA256 -LiteralPath $path).Hash.ToLowerInvariant()
    "$hash  $([System.IO.Path]::GetFileName($path))"
}
Write-Utf8NoBom $manifestPath (($manifestLines -join "`n") + "`n")

Write-Output "D094 image verification: PASS"
Write-Output "Image=$ImageTag"
Write-Output "ImageIndexDigest=$imageId"
Write-Output "RuntimeManifestDigest=$runtimeManifestDigest"
Write-Output "DirtyWorktree=$dirtyWorktree"
Write-Output "Evidence=$evidenceRoot"
