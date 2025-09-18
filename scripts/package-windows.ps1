param(
  [string]$AppName = "Print",
  [string]$AppVersion = "1.0.0",
  # Accepts "21" or "21.0.5" etc. We'll normalize internally when building URLs
  [string]$JfxVersion = "21.0.0",
  [ValidateSet("x64","x86")][string]$Arch = "x64",
  [string]$OutputDir = "dist",
  [string]$IconPath = "",
  [string]$MainJar = "print.jar",
  [string]$MainClass = "test.Program"
)

Set-StrictMode -Version Latest

$JdkPath = $env:JAVA_HOME
if (-not $JdkPath) {
    Throw "JAVA_HOME environment variable not set. Make sure the 'setup-java' action runs first."
}

Write-Host "Using JDK from: $JdkPath"

function Get-NormalizedJfxVersions {
  param([string]$ver)
  # Returns an array of plausible version strings to try, e.g., "21", "21.0.0"
  $list = @()
  if ($ver -match '^\d+$') { $list += $ver; $list += "$ver.0.0" }
  else { $list += $ver }
  return $list
}

function Get-JfxJmodsPath {
  param([string]$ver, [string]$arch, [string]$baseDir)

  $attempts = @()

  # Maven Central pattern
  $attempts += @{ Url = "https://repo1.maven.org/maven2/org/openjfx/javafx-jmods/$ver/javafx-jmods-$ver-windows-$arch.zip"; Zip = "javafx-jmods-$ver-windows-$arch.zip" }

  # Gluon download pattern (jmods bundle)
  $attempts += @{ Url = "https://download2.gluonhq.com/openjfx/$ver/openjfx-$ver`_windows-$arch`_bin-jmods.zip"; Zip = "openjfx-$ver`_windows-$arch`_bin-jmods.zip" }

  foreach ($a in $attempts) {
    $tmpZip = Join-Path $baseDir $a.Zip
    try {
      Write-Host "Attempting download: $($a.Url)"
      Invoke-WebRequest -Uri $a.Url -OutFile $tmpZip -UseBasicParsing -ErrorAction Stop
      if (Test-Path $tmpZip) {
        Write-Host "Downloaded: $tmpZip"
        $extractRoot = Join-Path $baseDir "build"
        if (-not (Test-Path $extractRoot)) { New-Item -ItemType Directory -Force -Path $extractRoot | Out-Null }
        Write-Host "Extracting $tmpZip to $extractRoot"
        Expand-Archive -Path $tmpZip -DestinationPath $extractRoot -Force
        Remove-Item $tmpZip -Force

        # Locate directory containing .jmod files
        $jmodFile = Get-ChildItem -Path $extractRoot -Recurse -File -Filter '*.jmod' | Select-Object -First 1
        if ($null -ne $jmodFile) {
          $jmodsPath = $jmodFile.DirectoryName
          Write-Host "Found JavaFX jmods at: $jmodsPath"
          return $jmodsPath
        } else {
          Write-Warning "No .jmod files found after extracting $($a.Zip). Trying next source..."
        }
      }
    } catch {
      Write-Warning "Download failed from $($a.Url): $($_.Exception.Message)"
      if (Test-Path $tmpZip) { Remove-Item $tmpZip -Force }
    }
  }

  return $null
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Try to locate or download JavaFX jmods
$candidateVersions = Get-NormalizedJfxVersions -ver $JfxVersion
$jfxJmodsPath = $null
foreach ($v in $candidateVersions) {
  $jfxJmodsPath = Get-JfxJmodsPath -ver $v -arch $Arch -baseDir $scriptDir
  if ($null -ne $jfxJmodsPath) { break }
}

if (-not $jfxJmodsPath) {
  Throw "Failed to download or locate JavaFX jmods for version(s) '$($candidateVersions -join ", ")' and arch '$Arch'."
}

Write-Host "Creating runtime image with jlink..."
$jlink = Join-Path $JdkPath 'bin\jlink.exe'
$runtimeOut = Join-Path $scriptDir "build\runtime-$Arch"
$modules = 'java.base,java.logging,java.desktop,java.xml,javafx.controls,javafx.graphics,javafx.base'

# Build module-path including JDK jmods and the located JavaFX jmods directory
$modulePath = "$JdkPath\jmods;$jfxJmodsPath"
Write-Host "Using module-path: $modulePath"

& $jlink --module-path $modulePath --add-modules $modules --output $runtimeOut --compress 2 --no-header-files --no-man-pages --strip-debug; if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "Running jpackage to create MSI..."
$jpackage = Join-Path $JdkPath 'bin\jpackage.exe'
$targetJarObj = Get-ChildItem -Path (Resolve-Path "$scriptDir\..\target") -Filter "*.jar" | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if (-not $targetJarObj) {
    Throw "Could not find jar in target/. Run mvn package first."
}

$targetJarName = $targetJarObj.Name

# Ensure destination directory exists before resolving to absolute path
$destDirCandidate = Join-Path $scriptDir "..\$OutputDir\$Arch"
if (-not (Test-Path $destDirCandidate)) {
  Write-Host "Creating destination directory: $destDirCandidate"
  New-Item -ItemType Directory -Force -Path $destDirCandidate | Out-Null
}
$destDir = (Resolve-Path $destDirCandidate).Path

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