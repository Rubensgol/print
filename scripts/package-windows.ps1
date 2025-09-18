<#
  scripts/package-windows.ps1

  Usage (on Windows PowerShell):
    Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
    .\scripts\package-windows.ps1 -AppVersion 1.0.0 -JfxVersion 21.0.0 -JdkPath 'C:\Program Files\Java\jdk-21'

  This script assumes:
  - You run it on Windows where jlink and jpackage are available in the specified JDK.
  - WiX Toolset is installed if you want MSI packaging.
  - The project's executable jar is produced by `mvn -DskipTests package` into `target/`.

  It will:
  - Build the project with Maven
  - Download JavaFX jmods for Windows x64 if not found
  - Create a runtime image with jlink including JavaFX
  - Run jpackage to create an MSI installer
#>

param(
  [string]$AppName = "Print",
  [string]$AppVersion = "1.0.0",
  [string]$JdkPath = "C:\Program Files\Java\jdk-21",
  [string]$JfxVersion = "21.0.0",
  [ValidateSet("x64","x86")][string]$Arch = "x64",
  [string]$OutputDir = "dist",
  [string]$IconPath = "",
  [string]$MainJar = "print.jar",
  [string]$MainClass = "test.Program",
  [string]$BaseDownloadUrl = "https://gluonhq.com/download/javafx-21-jmods/"
)

Set-StrictMode -Version Latest

Write-Host "Building project with Maven..."
& mvn -DskipTests package

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$jmodsDir = Join-Path $scriptDir "build\javafx-jmods-$JfxVersion-windows-$Arch"

if (-not (Test-Path $jmodsDir)) {
  Write-Host "JavaFX jmods for arch $Arch not found locally; downloading..."
  $zipName = "javafx-jmods-$JfxVersion-windows-$Arch.zip"
  $downloadUrl = "$BaseDownloadUrl$zipName"
  $tmpZip = Join-Path $scriptDir $zipName
  Write-Host "Downloading $downloadUrl"
  Invoke-WebRequest -Uri $downloadUrl -OutFile $tmpZip
  Write-Host "Extracting $tmpZip to $jmodsDir"
  Expand-Archive -Path $tmpZip -DestinationPath (Join-Path $scriptDir "build") -Force
  Remove-Item $tmpZip
}

Write-Host "Creating runtime image with jlink..."
# create a per-arch runtime output
$jlink = Join-Path $JdkPath 'bin\jlink.exe'
$runtimeOut = Join-Path $scriptDir "build\runtime-$Arch"

# Modules list: adapt to the modules your app needs; include javafx.controls/javafx.graphics
$modules = 'java.base,java.logging,java.desktop,java.xml,javafx.controls,javafx.graphics,javafx.base'

& $jlink --module-path "$JdkPath\jmods;$jmodsDir\jmods" --add-modules $modules --output $runtimeOut --compress 2 --no-header-files --no-man-pages --strip-debug

Write-Host "Running jpackage to create MSI..."

$jpackage = Join-Path $JdkPath 'bin\jpackage.exe'
$targetJarObj = Get-ChildItem -Path (Resolve-Path "$scriptDir\..\target") -Filter "*.jar" | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if (-not $targetJarObj) {
    Throw "Could not find jar in target/. Run mvn package first."
}

$targetJarName = $targetJarObj.Name

 # Put per-arch output directory
 $destDir = Resolve-Path (Join-Path $scriptDir "..\$OutputDir\$Arch")

 $args = @(
    '--name', $AppName,
    '--app-version', $AppVersion,
    '--input', (Resolve-Path "$scriptDir\..\target"),
    '--main-jar', $targetJarName,
    '--main-class', $MainClass,
    '--runtime-image', $runtimeOut,
    '--type', 'msi',
  '--dest', $destDir
)

if ($IconPath -ne "") { $args += @('--icon', $IconPath) }

Write-Host "jpackage args: $($args -join ' ')"
& $jpackage $args

Write-Host "Packaging finished. Installer in: $OutputDir"
