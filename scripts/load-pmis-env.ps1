$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $projectRoot "pmis-backend\.env"

if (-not (Test-Path -LiteralPath $envFile -PathType Leaf)) {
    throw "PMIS .env file not found."
}

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
        [Environment]::SetEnvironmentVariable($name, $value, "Process")
    }
}

$requiredVariables = @(
    "SPRING_DATASOURCE_PASSWORD",
    "JWT_SECRET",
    "RUNTIME_API_KEY"
)

foreach ($name in $requiredVariables) {
    $value = [Environment]::GetEnvironmentVariable($name, "Process")

    if ([string]::IsNullOrWhiteSpace($value)) {
        throw "$name is missing in PMIS environment."
    }
}

Write-Host "PMIS environment variables loaded successfully."