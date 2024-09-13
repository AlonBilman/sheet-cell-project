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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Set;
import java.util.function.Consumer;

public class TableFunctionalityController {

    private Boolean activeButtonsWhenLoadingFile;
    private Boolean activeButtonsWhenClickingCell;
    private Boolean activeButtonsWhenClickingRow;
    private Boolean activeButtonsWhenClickingColumn;

    private AppController appController;

    @FXML
    public Button resetButton;
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


    public enum ButtonState {
        LOADING_FILE,
        CLICKING_CELL,
        CLICKING_ROW,
        CLICKING_COLUMN
    }

    private enum confirmType {
        ADD_NEW_RANGE,
        DELETE_EXISTING_RANGE,
        VIEW_EXISTING_RANGE,
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
        resetButton.setDisable(!this.activeButtonsWhenClickingCell);
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
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOnAction(e -> {
            try {
                Color selectedColor = colorPicker.getValue();
                System.out.println("Selected Color: " + selectedColor);
                colorCallback.accept(selectedColor);
                popupStage.close();
            } catch (Exception ex) {/*ignore*/}
        });
        VBox vbox = new VBox(10, colorPicker);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));
        Scene scene = new Scene(vbox, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void buildModifySizePopup(String title, String promptText, boolean isColumn) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        TextField inputField = new TextField();
        VBox vbox = createModifySizePopupVBox(inputField, popupStage, promptText, isColumn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 320, 150);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private VBox createModifySizePopupVBox(TextField inputField, Stage popupStage, String promptText, boolean isColumn) {
        Label promptLabel = new Label(promptText);
        Button confirmButton = new Button(isColumn ? "Set Width" : "Set Height");
        confirmButton.setOnAction(event -> handleModifySizePopupConfirm(inputField, popupStage));
        return new VBox(20, promptLabel, inputField, confirmButton);
    }

    private void handleModifySizePopupConfirm(TextField inputField, Stage popupStage) {
        try {
            double newSize = Double.parseDouble(inputField.getText());
            if (newSize > 0) {
                appController.updateSize(newSize);
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
        buildModifySizePopup("Enter New Column Width", "Please input new width:", true);
    }

    @FXML
    private void setRowActionListener() {
        buildModifySizePopup("Enter New Row Height", "Please input new height:", false);
    }

    @FXML
    private void alignmentSetListener() {
        buildAlimentPopup();
        System.out.println("Alignment Set button clicked");
    }

    private void buildAlimentPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Set Alignment");

        RadioButton leftAlign = new RadioButton("Left");
        RadioButton centerAlign = new RadioButton("Center");
        RadioButton rightAlign = new RadioButton("Right");

        ToggleGroup alignmentGroup = new ToggleGroup();
        leftAlign.setToggleGroup(alignmentGroup);
        centerAlign.setToggleGroup(alignmentGroup);
        rightAlign.setToggleGroup(alignmentGroup);
        centerAlign.setSelected(true);

        Button confirmButton = getButtonForAlimentPopup(alignmentGroup, popupStage);

        VBox vbox = new VBox(10, leftAlign, centerAlign, rightAlign, confirmButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        Scene scene = new Scene(vbox, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private Button getButtonForAlimentPopup(ToggleGroup alignmentGroup, Stage popupStage) {
        Button confirmButton = new Button("Set Alignment");
        confirmButton.setOnAction(event -> {
            RadioButton selectedButton = (RadioButton) alignmentGroup.getSelectedToggle();
            if (selectedButton != null) {
                String alignment = selectedButton.getText();

                appController.setAlignment(alignment);
                popupStage.close();
            }
        });
        return confirmButton;
    }

    @FXML
    private void cellTextColorPick() {
        buildColorPickerPopup("Choose a Color", (selectedColor) -> {
            appController.textColorPicked(selectedColor);
        });
    }

    @FXML
    private void cellBackgroundColorPick() {
        buildColorPickerPopup("Choose a Color", (selectedColor) -> {
            appController.backgroundColorPicked(selectedColor);
        });
    }

    @FXML
    public void resetCellStyleListener(ActionEvent actionEvent) {
        appController.resetStyleClicked();
    }

    private Button createNewRangeButton(TextField rangeNameField, TextField fromCellField, TextField toCellField, Stage currStage) {
        Button button = new Button("Confirm");
        button.setOnAction(event -> {
            String rangeName = rangeNameField.getText();
            if (rangeName.trim().isEmpty())
                showInfoAlert("Error: Entered range name is empty.");
            else {
                String fromCell = fromCellField.getText();
                String toCell = toCellField.getText();
                try {
                    appController.addNewRange(rangeName, fromCell, toCell);
                    currStage.close();
                } catch (Exception e) {
                    showInfoAlert(e.getMessage());
                }
            }
        });
        return button;
    }

    @FXML
    private void addNewRangeListener() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Add New Range");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField rangeNameField = new TextField();
        rangeNameField.setPromptText("Name:");
        TextField fromCellField = new TextField();
        fromCellField.setPromptText("From: e.g., A1");
        TextField toCellField = new TextField();
        toCellField.setPromptText("To: e.g., A6");

        Button confirmButton = createNewRangeButton(rangeNameField, fromCellField, toCellField, popupStage);
        vbox.getChildren().addAll(rangeNameField, fromCellField, toCellField, confirmButton);

        Scene scene = new Scene(vbox, 300, 200);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    private void viewAndDeleteRangePopup(confirmType type) {
        Set<String> rangeNames = appController.getExistingRanges();
        if (rangeNames.isEmpty()) {
            showInfoAlert("Error: There are 0 Ranges right now");
            return;
        }
        Stage popupStage = new Stage();
        popupStage.setTitle("Select a Range");
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(10));
        ToggleGroup toggleGroup = new ToggleGroup();
        for (String rangeName : rangeNames) {
            RadioButton radioButton = new RadioButton(rangeName);
            radioButton.setToggleGroup(toggleGroup);
            vbox.getChildren().add(radioButton);
        }
        Button confirmButton = createViewOrDeleteRangeButton(type, popupStage, toggleGroup);
        confirmButton.setAlignment(Pos.CENTER);
        vbox.getChildren().add(confirmButton);
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 300, 200);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    private Button createViewOrDeleteRangeButton(confirmType type, Stage currStage, ToggleGroup toggleGroup) {
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
            if (selectedRadioButton != null) {
                String selectedRangeName = selectedRadioButton.getText();
                if (type.equals(confirmType.VIEW_EXISTING_RANGE)) {
                    // Show the range...calling the appController
                    System.out.println("Selected range: " + selectedRangeName);
                } else if (type.equals(confirmType.DELETE_EXISTING_RANGE)) {
                    // Delete the range from the system...
                    System.out.println("Selected range to delete: " + selectedRangeName);
                }
                currStage.close();
            } else {
                showInfoAlert("Please select a range before confirming.");
            }
        });
        return confirmButton;
    }

    @FXML
    private void viewExistingRangeListener() {
        viewAndDeleteRangePopup(confirmType.VIEW_EXISTING_RANGE);
    }

    @FXML
    private void deleteExistingRangeListener() {
        viewAndDeleteRangePopup(confirmType.DELETE_EXISTING_RANGE);
    }


}