<#
  scripts/package-windows.ps1
  Versão corrigida para uso em GitHub Actions
#>

param(
  [string]$AppName = "Print",
  [string]$AppVersion = "1.0.0",
  # CORREÇÃO 2: Removido JdkPath daqui, vamos usar a variável de ambiente.
  [string]$JfxVersion = "21.0.0",
  [ValidateSet("x64","x86")][string]$Arch = "x64",
  [string]$OutputDir = "dist",
  [string]$IconPath = "",
  [string]$MainJar = "print.jar",
  [string]$MainClass = "test.Program",
  # CORREÇÃO 1: URL base atualizada para o Maven Central.
  [string]$BaseDownloadUrl = "https://repo1.maven.org/maven2/org/openjfx/javafx-jmods"
)

Set-StrictMode -Version Latest

# CORREÇÃO 2: Usando a variável de ambiente JAVA_HOME que o GitHub Actions fornece.
$JdkPath = $env:JAVA_HOME
if (-not $JdkPath) {
    Throw "JAVA_HOME environment variable not set. Make sure the 'setup-java' action runs first."
}

Write-Host "Using JDK from: $JdkPath"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$jmodsDir = Join-Path $scriptDir "build\javafx-jmods-$JfxVersion-windows-$Arch"

if (-not (Test-Path $jmodsDir)) {
  Write-Host "JavaFX jmods for arch $Arch not found locally; downloading..."
  # CORREÇÃO 1: Nome do arquivo e URL montados para o padrão do Maven Central.
  $zipName = "javafx-jmods-$JfxVersion-windows-$Arch.zip"
  $downloadUrl = "$BaseDownloadUrl/$JfxVersion/$zipName"
  $tmpZip = Join-Path $scriptDir $zipName

  Write-Host "Downloading $downloadUrl"
  Invoke-WebRequest -Uri $downloadUrl -OutFile $tmpZip
  Write-Host "Extracting $tmpZip to $jmodsDir"
  Expand-Archive -Path $tmpZip -DestinationPath (Join-Path $scriptDir "build") -Force
  Remove-Item $tmpZip
}

Write-Host "Creating runtime image with jlink..."
$jlink = Join-Path $JdkPath 'bin\jlink.exe'
$runtimeOut = Join-Path $scriptDir "build\runtime-$Arch"
$modules = 'java.base,java.logging,java.desktop,java.xml,javafx.controls,javafx.graphics,javafx.base'
& $jlink --module-path "$JdkPath\jmods;$jmodsDir" --add-modules $modules --output $runtimeOut --compress 2 --no-header-files --no-man-pages --strip-debug; if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "Running jpackage to create MSI..."
$jpackage = Join-Path $JdkPath 'bin\jpackage.exe'
$targetJarObj = Get-ChildItem -Path (Resolve-Path "$scriptDir\..\target") -Filter "*.jar" | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if (-not $targetJarObj) {
    Throw "Could not find jar in target/. Run mvn package first."
}

$targetJarName = $targetJarObj.Name
$destDir = (Resolve-Path (Join-Path $scriptDir "..\$OutputDir\$Arch")).Path

if (-not (Test-Path $destDir)) {
    Write-Host "Creating destination directory: $destDir"
    New-Item -ItemType Directory -Force -Path $destDir
}

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
& $jpackage $args; if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "Packaging finished. Installer in: $destDir"