@echo off
set JAVAFX_LIB_PATH=..\ThirdParty\JAVAFX\openjfx-22.0.2_windows-x64_bin-sdk\javafx-sdk-22.0.2\lib

if not exist "%JAVAFX_LIB_PATH%" (
    echo Error: JavaFX library path not found at %JAVAFX_LIB_PATH%
    exit /b 1
)

java --module-path "%JAVAFX_LIB_PATH%" --add-modules javafx.controls,javafx.fxml -jar ui.javafx.jar
