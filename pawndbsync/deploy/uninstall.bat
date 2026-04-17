@echo off
:: ============================================================
::  PawnDbSync  –  Uninstaller
::  Run as Administrator
::  Usage:
::    uninstall.bat           interactive (prompts before deleting files)
::    uninstall.bat /silent   no prompts (used by Add/Remove Programs)
:: ============================================================
setlocal EnableDelayedExpansion

set SERVICE_DIR=C:\PawnDbSync
set SERVICE_ID=PawnDbSync
set REG_KEY=HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\%SERVICE_ID%
set SILENT=0

:: ---------- parse args ----------
if /i "%~1"=="/silent" set SILENT=1
if /i "%~1"=="-silent" set SILENT=1
if /i "%~1"=="/s"      set SILENT=1

:: ---------- privilege check ----------
net session >nul 2>&1
if errorlevel 1 (
    if "%SILENT%"=="1" (
        exit /b 1
    ) else (
        echo ERROR: Please run this script as Administrator.
        pause & exit /b 1
    )
)

:: ---------- confirm (interactive only) ----------
if "%SILENT%"=="0" (
    echo ============================================================
    echo  PawnDbSync Uninstaller
    echo ============================================================
    echo.
    echo  This will:
    echo    1. Stop the "%SERVICE_ID%" Windows service
    echo    2. Remove the service from Windows
    echo    3. Remove the entry from Add/Remove Programs
    echo    4. Delete all files in %SERVICE_DIR%
    echo.
    set /p CONFIRM="Are you sure you want to uninstall? [Y/N]: "
    if /i not "!CONFIRM!"=="Y" (
        echo Uninstall cancelled.
        pause & exit /b 0
    )
    echo.
)

:: ---------- stop service ----------
echo [1/4] Stopping service...
net stop %SERVICE_ID% >nul 2>&1
:: Wait a moment for the service to finish stopping
ping -n 3 127.0.0.1 >nul 2>&1

:: ---------- remove service ----------
echo [2/4] Removing Windows service...
if exist "%SERVICE_DIR%\pawndbsync.exe" (
    "%SERVICE_DIR%\pawndbsync.exe" uninstall >nul 2>&1
    if errorlevel 1 (
        :: Fallback to sc.exe if WinSW fails
        sc delete %SERVICE_ID% >nul 2>&1
    )
) else (
    sc delete %SERVICE_ID% >nul 2>&1
)

:: ---------- remove Add/Remove Programs entry ----------
echo [3/4] Removing Add/Remove Programs entry...
reg delete "%REG_KEY%" /f >nul 2>&1

:: ---------- delete install directory ----------
echo [4/4] Deleting installation files...
:: The uninstaller is inside SERVICE_DIR, so we use a self-deleting PowerShell trick
if exist "%SERVICE_DIR%" (
    powershell -NoProfile -Command ^
        "Start-Sleep -Seconds 2; Remove-Item -Recurse -Force '%SERVICE_DIR%'" >nul 2>&1
)

if "%SILENT%"=="0" (
    echo.
    echo ============================================================
    echo  PawnDbSync has been completely removed.
    echo ============================================================
    pause
)

exit /b 0
