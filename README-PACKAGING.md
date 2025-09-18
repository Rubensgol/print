Packaging and updates
=====================

This document explains how to create a Windows installer for the `print` application and how to publish/update using `update4j`.

Scripts & workflow added
- `scripts/package-windows.ps1`: PowerShell script that builds the project, downloads JavaFX jmods (Windows x64), runs `jlink` and `jpackage` to create an MSI installer.
- `.github/workflows/windows-package.yml`: GitHub Actions workflow that runs on `windows-latest` when you push a git tag `v*.*.*` or on manual dispatch. It builds, downloads JavaFX jmods, runs the packaging script, and uploads the generated installer as an artifact.
- `src/config/example-config.xml`: Example update4j manifest showing how to list files.
- `src/controler/business/atualizar/update4j/BootstrapperExample.java`: Example bootstrapper that reads a remote `config.xml` and applies updates using your existing `AtualizaUpdate4j`.

How to produce a Windows installer locally
-----------------------------------------
1. On a Windows machine with JDK 21 installed and WiX Toolset available, run:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\scripts\package-windows.ps1 -AppVersion 1.0.0 -JfxVersion 21.0.0 -JdkPath 'C:\Program Files\Java\jdk-21'
```

2. The resulting MSI will be under `dist/`.

How to build on CI (GitHub Actions)
----------------------------------
- Create a release tag like `v1.0.0` and push it. The workflow will run and upload the `dist` folder as an artifact.

Using update4j for incremental updates
--------------------------------------
- Generate a `config.xml` using the `CriaConfig` helper (provided) or edit `src/config/example-config.xml` with correct file URLs and checksums.
- Host the `config.xml` and the files it references on a secure HTTP server (S3, GitHub Pages, etc.).
- Ensure your application uses the `AtualizaUpdate4j` (already present) to check `temAtualizacao()` and call `atualiza()` when needed.

Security & tips
---------------
- Serve artifacts over HTTPS.
- Sign the `config.xml` using update4j signing support before publishing.
- Test the installer and the update flow on a clean Windows VM before wide distribution.
