@echo off
:: ============================================================
::  PawnDbSync  –  Windows Service Uninstaller
::  Run as Administrator
:: ============================================================
setlocal

set SERVICE_DIR=C:\PawnDbSync
set SERVICE_ID=PawnDbSync

net session >nul 2>&1
if errorlevel 1 (
    echo ERROR: Please run this script as Administrator.
    pause & exit /b 1
)

echo Stopping service (if running) ...
net stop %SERVICE_ID% 2>nul

echo Uninstalling service ...
"%SERVICE_DIR%\pawndbsync.exe" uninstall

echo.
set /p REMOVE="Delete %SERVICE_DIR% folder and all logs? [Y/N]: "
if /i "%REMOVE%"=="Y" (
    rmdir /S /Q "%SERVICE_DIR%"
    echo Folder deleted.
)

echo Done.
pause
