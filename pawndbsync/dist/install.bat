@echo off
:: ============================================================
::  PawnDbSync  –  Windows Service Installer
::  Run as Administrator
:: ============================================================
setlocal EnableDelayedExpansion

set SERVICE_DIR=C:\PawnDbSync
set SERVICE_ID=PawnDbSync
set WINSW_URL=https://github.com/winsw/winsw/releases/download/v2.12.0/WinSW-x64.exe

:: ---------- privilege check ----------
net session >nul 2>&1
if errorlevel 1 (
    echo ERROR: Please run this script as Administrator.
    pause & exit /b 1
)

:: ---------- java check ----------
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not found on PATH.
    echo Install JDK 17 and make sure it is on the system PATH, then re-run.
    pause & exit /b 1
)

:: ---------- create directories ----------
echo Creating %SERVICE_DIR% ...
mkdir "%SERVICE_DIR%"      2>nul
mkdir "%SERVICE_DIR%\logs" 2>nul

:: ---------- copy files ----------
echo Copying application files...
copy /Y "%~dp0pawndbsync.jar"          "%SERVICE_DIR%\pawndbsync.jar"          || goto :err
copy /Y "%~dp0pawndbsync.xml"          "%SERVICE_DIR%\pawndbsync.xml"          || goto :err
copy /Y "%~dp0application.properties"  "%SERVICE_DIR%\application.properties"  || goto :err

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

echo.
echo ============================================================
echo  SUCCESS – %SERVICE_ID% is installed and running.
echo  Install directory : %SERVICE_DIR%
echo  Logs              : %SERVICE_DIR%\logs
echo  Config            : %SERVICE_DIR%\application.properties
echo.
echo  Useful commands:
echo    net start   %SERVICE_ID%
echo    net stop    %SERVICE_ID%
echo    sc query    %SERVICE_ID%
echo ============================================================
pause
exit /b 0

:err
echo.
echo ERROR: Installation failed. See messages above.
pause
exit /b 1
