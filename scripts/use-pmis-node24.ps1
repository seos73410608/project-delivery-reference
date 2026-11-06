
# PMIS Node.js 24 Environment
# Applies only to the current PowerShell session.

$nodePath = Join-Path $PSScriptRoot "..\Tools\node-v24.19.0-win-x64"
$nodePath = (Resolve-Path $nodePath).Path

if (-not (Test-Path (Join-Path $nodePath "node.exe"))) {
    Write-Error "Node.js 24 was not found: $nodePath"
    return
}

$pathEntries = $env:Path -split ";"

if ($pathEntries -notcontains $nodePath) {
    $env:Path = "$nodePath;$env:Path"
}

Write-Host ""
Write-Host "PMIS Node.js 24 environment activated." -ForegroundColor Green
Write-Host "Node.js path: $nodePath"

Write-Host ""
Write-Host "Node.js version:"
node --version

Write-Host ""
Write-Host "npm version:"
npm --version
