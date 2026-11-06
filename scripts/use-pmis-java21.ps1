
# PMIS Java 21 Environment
# Applies only to the current PowerShell session.

$jdkPath = "C:\Program Files\Java\jdk-21"

if (-not (Test-Path "$jdkPath\bin\java.exe")) {
    Write-Error "JDK 21 was not found: $jdkPath"
    return
}

$env:JAVA_HOME = $jdkPath

$javaBin = "$jdkPath\bin"
$pathEntries = $env:Path -split ";"

if ($pathEntries -notcontains $javaBin) {
    $env:Path = "$javaBin;$env:Path"
}

Write-Host ""
Write-Host "PMIS Java 21 environment activated." -ForegroundColor Green
Write-Host "JAVA_HOME: $env:JAVA_HOME"

Write-Host ""
Write-Host "Java version:"
java -version
