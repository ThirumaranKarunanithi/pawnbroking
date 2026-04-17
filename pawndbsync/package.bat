@echo off
:: ============================================================
::  PawnDbSync  –  Build + Package distribution
::  Run from the project root (where pom.xml lives)
::  Output: dist\  (copy this folder to the target machine)
:: ============================================================
setlocal

echo ============================================================
echo  Step 1: Maven build
echo ============================================================
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
    echo.
    echo BUILD FAILED – fix compilation errors and re-run.
    pause & exit /b 1
)

echo.
echo ============================================================
echo  Step 2: Assemble dist\
echo ============================================================
rmdir /S /Q dist 2>nul
mkdir dist

copy /Y "target\pawndbsync.jar"             "dist\pawndbsync.jar"          || goto :err
copy /Y "deploy\pawndbsync.xml"             "dist\pawndbsync.xml"          || goto :err
copy /Y "deploy\application.properties"     "dist\application.properties"  || goto :err
copy /Y "deploy\install.bat"                "dist\install.bat"             || goto :err
copy /Y "deploy\uninstall.bat"              "dist\uninstall.bat"           || goto :err
copy /Y "deploy\README.txt"                 "dist\README.txt"              || goto :err

:: Bundle WinSW if already downloaded
if exist "deploy\winsw.exe" (
    copy /Y "deploy\winsw.exe" "dist\winsw.exe"
    echo Bundled winsw.exe
) else (
    echo NOTE: deploy\winsw.exe not found – install.bat will download it on the target machine.
    echo       To bundle it offline, download WinSW-x64.exe, rename to winsw.exe, place in deploy\
)

echo.
echo ============================================================
echo  dist\ is ready.  Copy the entire dist\ folder to the
echo  target machine, then run install.bat as Administrator.
echo ============================================================
explorer dist
pause
exit /b 0

:err
echo ERROR: Failed to copy files. See messages above.
pause
exit /b 1
