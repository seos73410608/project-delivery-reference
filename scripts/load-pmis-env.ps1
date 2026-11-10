
$ErrorActionPreference = "Stop"

# ============================================================
# PMIS Environment Loader
# ============================================================
# Loads PMIS environment variables into the current
# PowerShell process.
#
# Supported profiles:
#   - dev,postgresql
#   - prod,postgresql
#   - dev,mariadb
#   - prod,mariadb
#
# Database credentials:
#   - PMIS_DB_USERNAME
#   - PMIS_DB_PASSWORD
#
# ============================================================

$projectRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $projectRoot "pmis-backend\.env"

# ------------------------------------------------------------
# 1. Validate environment file
# ------------------------------------------------------------

if (-not (Test-Path -LiteralPath $envFile -PathType Leaf)) {
    throw "PMIS .env file not found."
}

# ------------------------------------------------------------
# 2. Load environment variables
# ------------------------------------------------------------

foreach ($line in Get-Content -LiteralPath $envFile -Encoding ASCII) {

    if ([string]::IsNullOrWhiteSpace($line)) {
        continue
    }

    if ($line.TrimStart().StartsWith("#")) {
        continue
    }

    $separatorIndex = $line.IndexOf("=")

    if ($separatorIndex -lt 1) {
        continue
    }

    $name = $line.Substring(0, $separatorIndex).Trim()
    $value = $line.Substring($separatorIndex + 1)

    if ($name -match '^[A-Za-z_][A-Za-z0-9_]*$') {
        [Environment]::SetEnvironmentVariable(
            $name,
            $value,
            "Process"
        )
    }
}

# ------------------------------------------------------------
# 3. Clear inherited datasource variables
# ------------------------------------------------------------

$variablesToClear = @(
    "SPRING_DATASOURCE_URL",
    "SPRING_DATASOURCE_USERNAME",
    "SPRING_DATASOURCE_PASSWORD",
    "PMIS_DB_USERNAME",
    "PMIS_DB_PASSWORD"
)

foreach ($name in $variablesToClear) {

    [Environment]::SetEnvironmentVariable(
        $name,
        $null,
        "Process"
    )
}

# ------------------------------------------------------------
# 4. Resolve active profiles
# ------------------------------------------------------------

$activeProfiles = [Environment]::GetEnvironmentVariable(
    "SPRING_PROFILES_ACTIVE",
    "Process"
)

if ([string]::IsNullOrWhiteSpace($activeProfiles)) {

    $activeProfiles = "dev,postgresql"

    [Environment]::SetEnvironmentVariable(
        "SPRING_PROFILES_ACTIVE",
        $activeProfiles,
        "Process"
    )
}

$profiles = @(
    $activeProfiles.Split(",") |
        ForEach-Object {
            $_.Trim().ToLowerInvariant()
        } |
        Where-Object {
            $_ -ne ""
        }
)

# ------------------------------------------------------------
# 5. Validate environment and database profiles
# ------------------------------------------------------------

$environmentProfiles = @(
    $profiles | Where-Object {
        $_ -in @("dev", "prod")
    }
)

$databaseProfiles = @(
    $profiles | Where-Object {
        $_ -in @("postgresql", "mariadb")
    }
)

if ($environmentProfiles.Count -ne 1) {
    throw "Exactly one environment profile is required: dev or prod."
}

if ($databaseProfiles.Count -ne 1) {
    throw "Exactly one database profile is required: postgresql or mariadb."
}

$environmentProfile = $environmentProfiles[0]
$databaseProfile = $databaseProfiles[0]

# ------------------------------------------------------------
# 6. Validate common required variables
# ------------------------------------------------------------

$requiredCommonVariables = @(
    "JWT_SECRET",
    "RUNTIME_API_KEY"
)

foreach ($name in $requiredCommonVariables) {

    $value = [Environment]::GetEnvironmentVariable(
        $name,
        "Process"
    )

    if ([string]::IsNullOrWhiteSpace($value)) {
        throw "$name is missing in PMIS environment."
    }
}

# ------------------------------------------------------------
# 7. Resolve database credentials
# ------------------------------------------------------------

$credentialPrefix = "PMIS_{0}_{1}" -f `
    $databaseProfile.ToUpperInvariant(), `
    $environmentProfile.ToUpperInvariant()

$usernameVariable = "${credentialPrefix}_USERNAME"
$passwordVariable = "${credentialPrefix}_PASSWORD"

$dbUsername = [Environment]::GetEnvironmentVariable(
    $usernameVariable,
    "Process"
)

$dbPassword = [Environment]::GetEnvironmentVariable(
    $passwordVariable,
    "Process"
)

if ([string]::IsNullOrWhiteSpace($dbUsername)) {
    throw "$usernameVariable is missing in PMIS environment."
}

if ([string]::IsNullOrWhiteSpace($dbPassword)) {
    throw "$passwordVariable is missing in PMIS environment."
}

# ------------------------------------------------------------
# 8. Set common database credentials
# ------------------------------------------------------------

[Environment]::SetEnvironmentVariable(
    "PMIS_DB_USERNAME",
    $dbUsername,
    "Process"
)

[Environment]::SetEnvironmentVariable(
    "PMIS_DB_PASSWORD",
    $dbPassword,
    "Process"
)

# ------------------------------------------------------------
# 9. Display result
# ------------------------------------------------------------

$ddlMode = if ($environmentProfile -eq "dev") {
    "update"
}
else {
    "validate"
}

Write-Host ""
Write-Host "PMIS environment variables loaded successfully." `
    -ForegroundColor Green

Write-Host "Active profiles: $($profiles -join ', ')" `
    -ForegroundColor Cyan

Write-Host "Environment: $environmentProfile" `
    -ForegroundColor Cyan

Write-Host "Database: $databaseProfile" `
    -ForegroundColor Cyan

Write-Host "Database username: $dbUsername" `
    -ForegroundColor Cyan

Write-Host "Hibernate DDL mode: $ddlMode" `
    -ForegroundColor Cyan
