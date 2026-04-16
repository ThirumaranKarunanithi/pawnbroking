@echo off
setlocal
cd /d "%~dp0"

echo =============================================
echo   PawnBroking Installer Builder
echo =============================================
echo.

:: Step 1: Maven build
echo [1/4] Building project with Maven...
call mvn clean package -q
if errorlevel 1 (
    echo ERROR: Maven build failed!
    pause & exit /b 1
)
echo Done.
echo.

:: Step 2: Prepare lib folder
echo [2/4] Preparing application files...

:: Copy main JAR into lib
copy /Y "target\PawnBroking-1.0-SNAPSHOT.jar" "target\lib\" > nul

:: Create jfx subfolder with ONLY JavaFX -win JARs (mirrors the run.bat module-path approach)
if exist "target\lib\jfx" rd /s /q "target\lib\jfx"
mkdir "target\lib\jfx"
copy /Y "target\javafx-lib\*.jar" "target\lib\jfx\" > nul

:: Remove all JavaFX JARs from the root lib (they live only in lib\jfx now)
del /q "target\lib\javafx-base-21.jar"      2>nul
del /q "target\lib\javafx-base-21-win.jar"  2>nul
del /q "target\lib\javafx-controls-21.jar"  2>nul
del /q "target\lib\javafx-controls-21-win.jar" 2>nul
del /q "target\lib\javafx-graphics-21.jar"  2>nul
del /q "target\lib\javafx-graphics-21-win.jar" 2>nul
del /q "target\lib\javafx-fxml-21.jar"      2>nul
del /q "target\lib\javafx-fxml-21-win.jar"  2>nul
del /q "target\lib\javafx-swing-21.jar"     2>nul
del /q "target\lib\javafx-swing-21-win.jar" 2>nul

if exist "target\app" rd /s /q "target\app"
echo Done.
echo.

:: Step 3: Create app-image with jpackage
echo [3/4] Creating app-image with jpackage...
"C:\Program Files\Java\jdk-17\bin\jpackage.exe" ^
    --type app-image ^
    --name "PawnBroking" ^
    --app-version "1.0.0" ^
    --vendor "Magizhchi" ^
    --input "target\lib" ^
    --dest "target\app" ^
    --main-jar "PawnBroking-1.0-SNAPSHOT.jar" ^
    --main-class "com.magizhchi.pawnbroking.Launcher" ^
    --icon "D:\Pawnbroking\icon\PawnBroking.ico" ^
    --java-options "--module-path $APPDIR\jfx" ^
    --java-options "--add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics" ^
    --java-options "-Dsun.java2d.uiScale=1"

if errorlevel 1 (
    echo ERROR: jpackage failed!
    pause & exit /b 1
)
echo Done.
echo.

:: Step 4: Build installer with Inno Setup
echo [4/4] Building installer with Inno Setup...
if not exist "target\installer" mkdir "target\installer"

"C:\Program Files (x86)\Inno Setup 5\ISCC.exe" "installer.iss"
if errorlevel 1 (
    echo ERROR: Inno Setup failed!
    echo Portable app is at: target\app\PawnBroking\
    pause & exit /b 1
)

echo.
echo =============================================
echo   SUCCESS!
echo   Installer: target\installer\PawnBrokingSetup.exe
echo   Portable:  target\app\PawnBroking\
echo =============================================
pause
