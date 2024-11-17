#!/bin/bash

JAVAFX_LIB_PATH="../ThirdParty/JAVAFX2/openjfx-22.0.2_windows-x64_bin-sdk/javafx-sdk-22.0.2/lib/"

if [ ! -d "$JAVAFX_LIB_PATH" ]; then
    echo "Error: JavaFX library path not found at $JAVAFX_LIB_PATH"
    exit 1
fi

java --module-path "$JAVAFX_LIB_PATH" --add-modules javafx.controls,javafx.fxml -jar ui.javafx.jar
