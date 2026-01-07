@echo off
cd /d "%~dp0"
setlocal

echo ===========================================
echo   SiUjian CBT - ULTIMATE BUILDER
echo ===========================================
echo.

:: 0. PRE-CHECK JRE
if not exist "READY_TO_DEPLOY\dist\jre" (
    echo [WARNING] Folder 'READY_TO_DEPLOY\dist\jre' tidak ditemukan!
    echo           Installer akan dibuat TANPA JRE jika kamu lanjut.
    echo           Gunakan 'BundleJRE.ps1' dulu jika mau package Java.
    timeout /t 3
) else (
    echo [INFO] JRE ditemukan. Siap bundle.
)

:: 1. COMPILE C# LAUNCHER
echo [1/5] Compiling Launcher (SiUjian.exe)...
set "CSC_PATH=C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe"
if not exist "%CSC_PATH%" set "CSC_PATH=C:\Windows\Microsoft.NET\Framework\v4.0.30319\csc.exe"

if exist "%CSC_PATH%" (
    "%CSC_PATH%" /target:winexe /out:SiUjian.exe /win32icon:READY_TO_DEPLOY\assets\logo.ico Launcher.cs
    if %errorlevel% neq 0 exit /b %errorlevel%
    echo     - Sukses Update SiUjian.exe
) else (
    echo     [WARNING] CSC Compiler gak nemu. Pake SiUjian.exe yg lama.
)
echo.

:: 2. DETEKSI WIX
set "WIX_BIN="
if defined WIX set "WIX_BIN=%WIX%bin"
if not defined WIX_BIN (
    if exist "C:\Program Files (x86)\WiX Toolset v3.11\bin" set "WIX_BIN=C:\Program Files (x86)\WiX Toolset v3.11\bin"
)
if "%WIX_BIN%"=="" (
    echo [ERROR] WiX Toolset tidak ditemukan. Install WiX v3.11 dulu!
    echo Build Selesai.
    exit /b
)

:: 3. HARVESTING (HEAT)
echo [2/5] Harvesting 'READY_TO_DEPLOY\dist' content...
"%WIX_BIN%\heat.exe" dir "READY_TO_DEPLOY\dist" -dr DISTFOLDER -cg DistContent -gg -g1 -sf -srd -var "var.DistSource" -t "HeatTransform.xslt" -out "DistFragment.wxs"
if %errorlevel% neq 0 (
    echo [ERROR] Gagal Heat Harvesting.
    echo Build Selesai.
    exit /b
)

:: 4. COMPILING (CANDLE)
echo [3/5] Compiling WXS files...
"%WIX_BIN%\candle.exe" -dDistSource="READY_TO_DEPLOY\dist" Product.wxs DistFragment.wxs -ext WixUIExtension
if %errorlevel% neq 0 (
    echo [ERROR] Gagal Compiling Product/Fragment.
    echo Build Selesai.
    exit /b
)

:: 4b. COMPILING BUNDLE
echo [3b/5] Compiling Bundle...
"%WIX_BIN%\candle.exe" Bundle.wxs -ext WixBalExtension
if %errorlevel% neq 0 (
    echo [ERROR] Gagal Compiling Bundle.
    echo Build Selesai.
    exit /b
)

:: 5. LINKING MSI (LIGHT)
echo [4/5] Linking into MSI...
"%WIX_BIN%\light.exe" -out SiUjian_Setup.msi Product.wixobj DistFragment.wixobj -ext WixUIExtension -b "READY_TO_DEPLOY\dist" -v -sval
if %errorlevel% neq 0 (
    echo [ERROR] Gagal Linking MSI.
    echo Build Selesai.
    exit /b
)

:: 6. LINKING BUNDLE (EXE)
echo [5/5] Linking into SETUP.EXE...
"%WIX_BIN%\light.exe" -out SiUjian_Setup.exe Bundle.wixobj -ext WixBalExtension
if %errorlevel% neq 0 (
    echo [ERROR] Gagal Linking Setup EXE.
    echo Build Selesai.
    exit /b
)

echo.
echo ===========================================
echo   SUKSES BESAR!
echo   Installer Siap: SiUjian_Setup.msi
echo ===========================================
echo Build Selesai.
