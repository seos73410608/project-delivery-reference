
# PMIS Development Environment Initialization

$ErrorActionPreference = "Stop"

# 1. Backend Spring Profile
$env:SPRING_PROFILES_ACTIVE = "test,postgresql"

# 2. Load PMIS Environment Variables
. "$PSScriptRoot\load-pmis-env.ps1"

# 3. Configure Java 21
. "$PSScriptRoot\use-pmis-java21.ps1"

# 4. Configure Node.js 24
. "$PSScriptRoot\use-pmis-node24.ps1"

Write-Host ""
Write-Host "PMIS Development Environment Ready" -ForegroundColor Green
Write-Host "SPRING_PROFILES_ACTIVE=$env:SPRING_PROFILES_ACTIVE"
