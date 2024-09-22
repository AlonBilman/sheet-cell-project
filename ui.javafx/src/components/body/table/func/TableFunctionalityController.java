package components.body.table.func;

import components.main.AppController;
import dto.CellDataDTO;
import expression.api.ObjType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public class TableFunctionalityController {

    @FXML
    private VBox tableFuncVBox;
    private String columnToFilter;
    private Boolean activeButtonsWhenLoadingFile;
    private Boolean activeButtonsWhenClickingCell;
    private Boolean activeButtonsWhenClickingRow;
    private Boolean activeButtonsWhenClickingColumn;

    private AppController appController;

    @FXML
    private Button sortButton;
    @FXML
    private Button filterButton;
    @FXML
    private Button resetButton;
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
        filterButton.setDisable(!this.activeButtonsWhenLoadingFile);
        sortButton.setDisable(!this.activeButtonsWhenLoadingFile);
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

    public void buildNoNameRangePopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("First, Provide the range!");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        TextField fromCellField = new TextField();
        fromCellField.setPromptText("From: e.g., A1");
        TextField toCellField = new TextField();
        toCellField.setPromptText("To: e.g., A6");
        setUnfocused(fromCellField, toCellField);
        Button confirmButton = createNewRangeButton(null, fromCellField, toCellField, popupStage, true);
        vbox.getChildren().addAll(fromCellField, toCellField, confirmButton);
        Scene scene = new Scene(vbox, 300, 200);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality.css")).toExternalForm());
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    public void filterColumnPopup(Set<CellDataDTO> cells, String fromCell, String toCell) {
        Set<String> colToFilterBy = new HashSet<>();
        Stage popupStage = new Stage();
        popupStage.setTitle("Select columns to filter by:");
        VBox vbox = new VBox();
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = fromCell.charAt(0); i <= toCell.charAt(0); i++) {
            CheckBox checkBox = new CheckBox(String.valueOf((char) i).toUpperCase());
            checkBoxes.add(checkBox);
            vbox.getChildren().add(checkBox);
        }
        Button confirmButton = new Button("Confirm Selection");
        confirmButton.setOnAction(e -> {
            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected())
                    colToFilterBy.add(checkBox.getText());
            }
            if (colToFilterBy.isEmpty()) {
                showInfoAlert("No columns selected");
                popupStage.toFront();
            }
            else {
                Map<String, Set<String>> columnToCellValues = getValuesFromCols(colToFilterBy, cells);
                confirmButton.setDisable(true);
                filterValuesPopup(fromCell,toCell,columnToCellValues);
                popupStage.close();
            }
        });
        vbox.getChildren().add(confirmButton);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        popupStage.setScene(new Scene(scrollPane, 300, 300));
        popupStage.showAndWait();
    }

    private void filterValuesPopup(String fromCell, String toCell,Map<String, Set<String>> columnToCellValues) {
        if (columnToCellValues.isEmpty()) {
            showInfoAlert("There are no values in these column(s)");
            return;
        }
        Stage popupStage = new Stage();
        popupStage.setTitle("Select Cell Values");
        HBox hbox = new HBox(30);
        hbox.setAlignment(Pos.CENTER);
        Map<String, Set<String>> selectedValues = new HashMap<>();
        columnToCellValues.forEach((column, cellValues) -> {
            VBox columnVbox = new VBox(10);
            columnVbox.setAlignment(Pos.CENTER_LEFT);
            columnVbox.getChildren().add(new Label("Column: " + column));
            Set<String> selectedColumnValues = new HashSet<>();
            selectedValues.put(column, selectedColumnValues);
            cellValues.stream()
                    .filter(value -> !value.isEmpty())
                    .forEach(value -> {
                        CheckBox checkBox = new CheckBox(value);
                        checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                            if (isSelected) {
                                selectedColumnValues.add(value);
                            } else {
                                selectedColumnValues.remove(value);
                            }
                        });
                        columnVbox.getChildren().add(checkBox);
                    });
            hbox.getChildren().add(columnVbox);
        });
        Button confirmButton = new Button("Confirm Selection");
        confirmButton.setOnAction(e -> {
            appController.filterParamsConfirmed(fromCell,toCell, selectedValues);
            popupStage.close();
        });
        VBox mainVbox = new VBox(20, hbox, confirmButton);
        mainVbox.setAlignment(Pos.CENTER);
        ScrollPane scrollPane = new ScrollPane(mainVbox);
        scrollPane.setFitToWidth(true);
        popupStage.setScene(new Scene(scrollPane, 500, 400));
        popupStage.showAndWait();
    }

    private Map<String, Set<String>> getValuesFromCols(Set<String> colToFilterBy, Set<CellDataDTO> cells) {
        Map<String, Set<String>> columnToCellValues = new HashMap<>();
        for (CellDataDTO cell : cells) {
            if (!cell.getEffectiveValue().getObjType().equals(ObjType.EMPTY) && colToFilterBy.contains(cell.getCol())) {
                columnToCellValues.computeIfAbsent(cell.getCol(), k -> new HashSet<>()).add(cell.getEffectiveValue().getValue().toString());
            }
        }
        return columnToCellValues;
    }


    public void filterButtonListener(ActionEvent actionEvent) {
        appController.filterButtonClicked();
    }

    public void sortButtonListener(ActionEvent actionEvent) {
        //appController.sortButtonClicked();
    }

    private void buildColorPickerPopup(Consumer<Color> colorCallback) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Choose a Color");
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

    private void changeButtonStyle(Button button, AppController.Style style) {
        switch (style) {
            case DEFAULT_STYLE:
                button.getStyleClass().add("button");
                break;
            case DARK_MODE:
                button.getStyleClass().add("button-dark-mode");
                break;
        }
    }

    private void buildModifySizePopup(String title, String promptText, boolean isColumn) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        TextField inputField = new TextField();
        inputField.setPromptText("Input new size here");
        setUnfocused(inputField);
        VBox vbox = createModifySizePopupVBox(inputField, popupStage, promptText, isColumn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 320, 150);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality.css")).toExternalForm());
        popupStage.setScene(scene);
        popupStage.show();
    }

    private VBox createModifySizePopupVBox(TextField inputField, Stage popupStage, String promptText, boolean isColumn) {
        Label promptLabel = new Label(promptText);
        Button confirmButton = new Button(isColumn ? "Set Width" : "Set Height");
        changeButtonStyle(confirmButton, appController.getStyleChosen());
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
    }

    private void buildAlimentPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Set Alignment");

        RadioButton leftAlign = new RadioButton("Left");
        RadioButton centerAlign = new RadioButton("Center");
        RadioButton rightAlign = new RadioButton("Right");

        ToggleGroup alignmentGroup = new ToggleGroup();
        centerAlign.setToggleGroup(alignmentGroup);
        rightAlign.setToggleGroup(alignmentGroup);

        Button confirmButton = getButtonForAlimentPopup(alignmentGroup, popupStage);

        VBox vbox = new VBox(10, leftAlign, centerAlign, rightAlign, confirmButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        Scene scene = new Scene(vbox, 300, 150);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality.css")).toExternalForm());
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private Button getButtonForAlimentPopup(ToggleGroup alignmentGroup, Stage popupStage) {
        Button confirmButton = new Button("Set Alignment");
        changeButtonStyle(confirmButton, appController.getStyleChosen());
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
        buildColorPickerPopup((selectedColor) -> appController.textColorPicked(selectedColor));
    }

    @FXML
    private void cellBackgroundColorPick() {
        buildColorPickerPopup((selectedColor) -> appController.backgroundColorPicked(selectedColor));
    }

    @FXML
    public void resetCellStyleListener(ActionEvent actionEvent) {
        appController.resetStyleClicked();
    }

    private Button createNewRangeButton(TextField rangeNameField, TextField fromCellField, TextField toCellField, Stage currStage, boolean noName) {
        Button button = new Button("Confirm");
        changeButtonStyle(button, appController.getStyleChosen());
        button.setOnAction(event -> {
            try {
                String fromCell = fromCellField.getText().trim();
                String toCell = toCellField.getText().trim();
                if (fromCell.isEmpty() || toCell.isEmpty()) {
                    showInfoAlert("Error: Both 'From' and 'To' cells must be provided.");
                    return;
                }
                if (noName) {
                    button.setDisable(true);
                    appController.noNameRangeSelected(fromCell, toCell);
                } else {
                    String rangeName = rangeNameField.getText().trim();
                    if (rangeName.isEmpty()) {
                        showInfoAlert("Error: Entered range name is empty.");
                    } else appController.addNewRange(rangeName, fromCell, toCell);
                }
                currStage.close();
            } catch (Exception e) {
                showInfoAlert(e.getMessage());
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
        rangeNameField.setPromptText("Enter new range name");
        TextField fromCellField = new TextField();
        fromCellField.setPromptText("From: e.g., A1");
        TextField toCellField = new TextField();
        toCellField.setPromptText("To: e.g., A6");

        setUnfocused(rangeNameField, fromCellField, toCellField);
        Button confirmButton = createNewRangeButton(rangeNameField, fromCellField, toCellField, popupStage, false);
        vbox.getChildren().addAll(rangeNameField, fromCellField, toCellField, confirmButton);

        Scene scene = new Scene(vbox, 300, 200);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality.css")).toExternalForm());
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    private void setUnfocused(Node... nodes) {
        for (Node node : nodes) {
            node.setFocusTraversable(false);
        }
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
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality.css")).toExternalForm());
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    private Button createViewOrDeleteRangeButton(confirmType type, Stage currStage, ToggleGroup toggleGroup) {
        Button confirmButton = new Button("Confirm");
        confirmButton.getStyleClass().add("button");
        confirmButton.setOnAction(e -> {
            RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
            if (selectedRadioButton != null) {
                String selectedRangeName = selectedRadioButton.getText();
                if (type.equals(confirmType.VIEW_EXISTING_RANGE)) {
                    appController.showRangeConfirmedClicked(selectedRangeName);
                } else if (type.equals(confirmType.DELETE_EXISTING_RANGE)) {
                    appController.deleteRangeConfirmedClicked(selectedRangeName);
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

    public void updateStyleOfVBox(AppController.Style style) {
        tableFuncVBox.getStyleClass().clear();
        changeVBoxStyle(style);
    }

    private void changeVBoxStyle(AppController.Style style) {
        switch (style) {
            case DEFAULT_STYLE -> {
                tableFuncVBox.getStyleClass().add("vbox");
                break;
            }
            case DARK_MODE -> {
                tableFuncVBox.getStyleClass().add("vbox-dark-mode");
                break;
            }
        }
    }


}