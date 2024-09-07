package components.body.table.func;

import components.main.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.function.Consumer;

public class TableFunctionalityController {

    private Boolean activeButtonsWhenLoadingFile;
    private Boolean activeButtonsWhenClickingCell;
    private Boolean activeButtonsWhenClickingRow;
    private Boolean activeButtonsWhenClickingColumn;

    private AppController appController;

    @FXML
    private Button setColButton;
    @FXML
    private Button setRowButton;
    @FXML
    private Button alignmentSetButton;
    @FXML
    private Button cellTextPick;

    @FXML
    private Button cellBackgroundPick;
    @FXML
    private Button addNewRangeButton;
    @FXML
    private Button deleteExistingRangeButton;
    @FXML
    private Button viewExistingRangeButton;

    public void AlignmentSetListener(ActionEvent actionEvent) {
    }

    public enum ButtonState {
        LOADING_FILE,
        CLICKING_CELL,
        CLICKING_ROW,
        CLICKING_COLUMN
    }

    public void initialize() {
        activeButtonsWhenLoadingFile = false;
        activeButtonsWhenClickingCell = false;
        activeButtonsWhenClickingRow = false;
        activeButtonsWhenClickingColumn = false;
    }

    public void outOfFocus() {
        activeButtonsWhenClickingCell = false;
        activeButtonsWhenClickingRow = false;
        activeButtonsWhenClickingColumn = false;
        updateButtonStates();
    }

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void updateButtonStates() {
        setColButton.setDisable(!this.activeButtonsWhenClickingColumn);
        setRowButton.setDisable(!this.activeButtonsWhenClickingRow);
        alignmentSetButton.setDisable(!(this.activeButtonsWhenClickingColumn || this.activeButtonsWhenClickingRow));
        cellTextPick.setDisable(!this.activeButtonsWhenClickingCell);
        cellBackgroundPick.setDisable(!this.activeButtonsWhenClickingCell);
        addNewRangeButton.setDisable(!this.activeButtonsWhenLoadingFile);
        deleteExistingRangeButton.setDisable(!this.activeButtonsWhenLoadingFile);
        viewExistingRangeButton.setDisable(!this.activeButtonsWhenLoadingFile);
    }

    public void setActiveButtons(ButtonState state, boolean isActive) {
        switch (state) {
            case LOADING_FILE:
                this.activeButtonsWhenLoadingFile = isActive;
                break;
            case CLICKING_CELL:
                this.activeButtonsWhenClickingCell = isActive;
                break;
            case CLICKING_ROW:
                this.activeButtonsWhenClickingRow = isActive;
                break;
            case CLICKING_COLUMN:
                this.activeButtonsWhenClickingColumn = isActive;
                break;
        }
        updateButtonStates();
    }

    public void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void buildColorPickerPopup(String title, Consumer<Color> colorCallback) {
        // Create a new popup stage
        Stage popupStage = new Stage();
        popupStage.setTitle(title);

        // Create a ColorPicker
        ColorPicker colorPicker = new ColorPicker();

        // Handle color selection
        colorPicker.setOnAction(e -> {
            try {
                // Get the selected color
                Color selectedColor = colorPicker.getValue();
                System.out.println("Selected Color: " + selectedColor);

                // Pass the selected color to the callback
                colorCallback.accept(selectedColor);

                // Close the popup after the color is picked
                popupStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Create a layout to hold the ColorPicker
        VBox vbox = new VBox(10, colorPicker);  // VBox with spacing of 10px
        vbox.setAlignment(Pos.CENTER);  // Center align the ColorPicker
        vbox.setPadding(new Insets(15));  // Add padding around the layout

        // Create the scene and set it in the stage
        Scene scene = new Scene(vbox, 300, 150);  // Set the scene size (width and height)
        popupStage.setScene(scene);

        // Show the popup window
        popupStage.show();
    }

    private void buildPopup(String title, String promptText, boolean isColumn) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        TextField inputField = new TextField();
        VBox vbox = createPopupVBox(inputField, popupStage, promptText, isColumn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 320, 150);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private VBox createPopupVBox(TextField inputField, Stage popupStage, String promptText, boolean isColumn) {
        Label promptLabel = new Label(promptText);
        Button confirmButton = new Button(isColumn ? "Set Width" : "Set Height");
        confirmButton.setOnAction(event -> handlePopupConfirm(inputField, popupStage, isColumn));
        return new VBox(20, promptLabel, inputField, confirmButton);
    }

    private void handlePopupConfirm(TextField inputField, Stage popupStage, boolean isColumn) {
        try {
            int newSize = Integer.parseInt(inputField.getText());
            if (newSize > 0) {
                // Notify AppController about the new size
                popupStage.close();
            } else {
                showInfoAlert("Error: Entered 0 or a negative value.");
            }
        } catch (NumberFormatException e) {
            showInfoAlert("Error: Entered value is not a number.");
        }
    }

    @FXML
    private void setColActionListener() {
        buildPopup("Enter New Column Width", "Please input new width:", true);
    }

    @FXML
    private void setRowActionListener() {
        buildPopup("Enter New Row Height", "Please input new height:", false);
    }

    @FXML
    private void alignmentSetListener() {
        System.out.println("Alignment Set button clicked");
    }

    @FXML
    private void cellTextColorPick() {
        buildColorPickerPopup("Choose a Color", (selectedColor) -> {
            // Use the color, e.g., apply it to an element or pass it to the appController
            appController.applyColor(selectedColor);
        });
    }
    @FXML
    private void cellBackgroundColorPick() {
        buildColorPickerPopup("Choose a Color", (selectedColor) -> {
            // Use the color, e.g., apply it to an element or pass it to the appController
            appController.applyColor(selectedColor);
        });
    }

    @FXML
    private void addNewRangeListener() {
        System.out.println("Add Range Set button clicked");
    }

    @FXML
    private void deleteExistingRangeListener() {
        System.out.println("Delete Range button clicked");
    }

    @FXML
    private void viewExistingRangeListener() {
        System.out.println("View Range button clicked");
    }

}