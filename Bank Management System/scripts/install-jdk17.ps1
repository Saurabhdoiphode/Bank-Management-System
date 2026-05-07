<#
install-jdk17.ps1
Downloads and installs a portable Temurin JDK 17 into the workspace and sets JAVA_HOME
Usage (from project root PowerShell):
  .\scripts\install-jdk17.ps1          # installs into .\.jdk and sets JAVA_HOME for current session
  Start PowerShell as Administrator and run with -System to register JAVA_HOME system-wide:
  .\scripts\install-jdk17.ps1 -System
#>

param(
    [switch]$System
)

$ErrorActionPreference = 'Stop'
$workspace = (Get-Location).Path
$targetDir = Join-Path $workspace '.jdk'
$zipPath = Join-Path $targetDir 'jdk17.zip'
# Candidate download URLs (tried in order)
$urls = @(
    'https://github.com/adoptium/temurin17-binaries/releases/latest/download/OpenJDK17U-jdk_x64_windows_hotspot_latest.zip',
    'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8+7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.8_7.zip',
    'https://cdn.azul.com/zulu/bin/zulu17.54.19-ca-jdk17.0.8-win_x64.zip',
    'https://download.bell-sw.com/java/17.0.8+7/bellsoft-jdk17.0.8-windows-amd64.zip'
)

Write-Host "Creating target dir: $targetDir"
New-Item -ItemType Directory -Force -Path $targetDir | Out-Null

$downloaded = $false
foreach ($u in $urls) {
    try {
        Write-Host "Attempting download: $u"
        Invoke-WebRequest -Uri $u -OutFile $zipPath -UseBasicParsing -TimeoutSec 120
        $downloaded = $true
        break
    } catch {
        Write-Warning "Download failed for $u - trying next: $($_.Exception.Message)"
    }
}

if (-not $downloaded) {
    Write-Error "All automatic download attempts failed. Please download a JDK 17 ZIP from https://adoptium.net/ or https://bell-sw.com/ and place it at $zipPath then re-run this script."
    exit 1
}

try {
    Write-Host "Extracting..."
    Expand-Archive -Path $zipPath -DestinationPath $targetDir -Force
} catch {
    Write-Error "Extract failed: $_"
    exit 1
}

# Find extracted JDK directory
$jdkDir = Get-ChildItem -Directory $targetDir | Where-Object { $_.Name -match 'jdk' -or $_.Name -match 'jre' } | Select-Object -First 1
if (-not $jdkDir) {
    Write-Error "Unable to locate extracted JDK directory under $targetDir"
    exit 1
}

$javaHome = Join-Path $targetDir $jdkDir.Name
Write-Host "Setting JAVA_HOME for current session to: $javaHome"
$env:JAVA_HOME = $javaHome
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

if ($System) {
    try {
        Write-Host "Attempting to set system JAVA_HOME via setx (requires admin)..."
        setx JAVA_HOME "$javaHome" /M | Out-Null
        $newPath = "$javaHome\bin;%PATH%"
        setx PATH "$newPath" /M | Out-Null
        Write-Host "System JAVA_HOME set. You may need to restart your shell or log out/in for changes to apply."
    } catch {
        Write-Warning "Failed to set system environment variables. Re-run this script as Administrator or set JAVA_HOME manually."
    }
}

Write-Host "JDK 17 installed at: $javaHome"
Write-Host "Run the build now with: mvn -f backend\pom.xml clean test-compile"
exit 0
