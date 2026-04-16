@echo off
cd /d "%~dp0"
java --module-path "target/javafx-lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics ^
     -Dsun.java2d.uiScale=1 ^
     -cp "target/lib/*;target/PawnBroking-1.0-SNAPSHOT.jar" ^
     com.magizhchi.pawnbroking.Launcher
pause
