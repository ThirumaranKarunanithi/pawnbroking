@echo off
:: ============================================================
::  PawnDbSync  –  Windows Service Installer
::  Run as Administrator
:: ============================================================
setlocal EnableDelayedExpansion

set SERVICE_DIR=C:\PawnDbSync
set SERVICE_ID=PawnDbSync
set DISPLAY_NAME=Pawn DB Sync Service
set VERSION=1.0.0
set PUBLISHER=Magizhchi Academy
set WINSW_URL=https://github.com/winsw/winsw/releases/download/v2.12.0/WinSW-x64.exe
set REG_KEY=HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\%SERVICE_ID%

:: ---------- privilege check ----------
net session >nul 2>&1
if errorlevel 1 (
    echo ERROR: Please run this script as Administrator.
    pause & exit /b 1
)

:: ---------- already installed? ----------
sc query %SERVICE_ID% >nul 2>&1
if not errorlevel 1 (
    echo WARNING: Service %SERVICE_ID% is already installed.
    set /p REINSTALL="Re-install / upgrade it? [Y/N]: "
    if /i not "!REINSTALL!"=="Y" (
        echo Cancelled.
        pause & exit /b 0
    )
    echo Stopping and removing existing service...
    net stop %SERVICE_ID% >nul 2>&1
    "%SERVICE_DIR%\pawndbsync.exe" uninstall >nul 2>&1
)

:: ---------- java check ----------
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not found on PATH.
    echo Install JDK 17 and ensure it is on the system PATH, then re-run.
    pause & exit /b 1
)

:: ---------- create directories ----------
echo Creating %SERVICE_DIR% ...
mkdir "%SERVICE_DIR%"      2>nul
mkdir "%SERVICE_DIR%\logs" 2>nul

:: ---------- copy application files ----------
echo Copying application files...
copy /Y "%~dp0pawndbsync.jar"          "%SERVICE_DIR%\pawndbsync.jar"          || goto :err
copy /Y "%~dp0pawndbsync.xml"          "%SERVICE_DIR%\pawndbsync.xml"          || goto :err
copy /Y "%~dp0application.properties"  "%SERVICE_DIR%\application.properties"  || goto :err

:: ---------- copy uninstaller into install dir ----------
copy /Y "%~dp0uninstall.bat"           "%SERVICE_DIR%\uninstall.bat"           || goto :err

:: ---------- get WinSW ----------
if exist "%~dp0winsw.exe" (
    echo Using bundled winsw.exe ...
    copy /Y "%~dp0winsw.exe" "%SERVICE_DIR%\pawndbsync.exe" || goto :err
) else (
    echo winsw.exe not found locally – downloading from GitHub...
    powershell -NoProfile -Command ^
        "Invoke-WebRequest -Uri '%WINSW_URL%' -OutFile '%SERVICE_DIR%\pawndbsync.exe'"
    if errorlevel 1 (
        echo ERROR: Download failed. Place winsw.exe next to install.bat and re-run.
        pause & exit /b 1
    )
)

:: ---------- install & start service ----------
echo Installing Windows service ...
"%SERVICE_DIR%\pawndbsync.exe" install
if errorlevel 1 goto :err

echo Starting service ...
net start %SERVICE_ID%
if errorlevel 1 goto :err

:: ---------- register in Add/Remove Programs ----------
echo Registering in Add/Remove Programs...
reg add "%REG_KEY%" /v "DisplayName"     /t REG_SZ    /d "%DISPLAY_NAME%"                              /f >nul
reg add "%REG_KEY%" /v "DisplayVersion"  /t REG_SZ    /d "%VERSION%"                                   /f >nul
reg add "%REG_KEY%" /v "Publisher"       /t REG_SZ    /d "%PUBLISHER%"                                 /f >nul
reg add "%REG_KEY%" /v "InstallLocation" /t REG_SZ    /d "%SERVICE_DIR%"                               /f >nul
reg add "%REG_KEY%" /v "UninstallString" /t REG_SZ    /d "\"%SERVICE_DIR%\uninstall.bat\""             /f >nul
reg add "%REG_KEY%" /v "QuietUninstallString" /t REG_SZ /d "\"%SERVICE_DIR%\uninstall.bat\" /silent"   /f >nul
reg add "%REG_KEY%" /v "NoModify"        /t REG_DWORD /d 1                                             /f >nul
reg add "%REG_KEY%" /v "NoRepair"        /t REG_DWORD /d 1                                             /f >nul
reg add "%REG_KEY%" /v "DisplayIcon"     /t REG_SZ    /d "%SERVICE_DIR%\pawndbsync.exe"                /f >nul

echo.
echo ============================================================
echo  SUCCESS – %SERVICE_ID% is installed and running.
echo.
echo  Install directory : %SERVICE_DIR%
echo  Logs              : %SERVICE_DIR%\logs
echo  Config            : %SERVICE_DIR%\application.properties
echo.
echo  Manage:
echo    net start %SERVICE_ID%
echo    net stop  %SERVICE_ID%
echo    sc query  %SERVICE_ID%
echo.
echo  Status API : http://localhost:8081/api/sync/status
echo               http://localhost:8081/api/files/status
echo.
echo  To uninstall: Settings > Apps  OR  run uninstall.bat as Admin
echo ============================================================
pause
exit /b 0

:err
echo.
echo ERROR: Installation failed. See messages above.
pause
exit /b 1
