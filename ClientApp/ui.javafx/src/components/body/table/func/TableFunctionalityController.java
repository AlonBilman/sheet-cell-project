package components.body.table.func;

import components.page.view.sheetscreen.AppController;
import dto.CellDataDTO;
import expression.api.ObjType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class TableFunctionalityController {

    private Boolean activeButtonsWhenLoadingFile;
    private Boolean activeButtonsWhenClickingCell;
    private Boolean activeButtonsWhenClickingRow;
    private Boolean activeButtonsWhenClickingColumn;

    private AppController appController;

    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Button createChartButton;
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

    private String cssLoad = "Default";


    public enum ButtonState {
        LOADING_FILE,
        CLICKING_CELL,
        CLICKING_ROW,
        CLICKING_COLUMN
    }

    public enum ConfirmType {
        ADD_NEW_RANGE,
        DELETE_EXISTING_RANGE,
        VIEW_EXISTING_RANGE,
        SORT_RANGE,
        FILTER_RANGE,
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

    // Method to change the theme dynamically
    public void setTheme(String newTheme) {
        cssSet(newTheme);
        // Clear the existing stylesheets
        scrollPane.getStylesheets().clear();

        // Add the new theme stylesheet
        scrollPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality" + newTheme + ".css"))
                        .toExternalForm()
        );
    }

    private void cssSet(String newTheme) {
        this.cssLoad = newTheme;
    }

    public void updateButtonStates() {
        setColButton.setDisable(!this.activeButtonsWhenClickingColumn);
        setRowButton.setDisable(!this.activeButtonsWhenClickingRow);
        alignmentSetButton.setDisable(!(this.activeButtonsWhenClickingColumn || this.activeButtonsWhenClickingRow));
        cellTextPick.setDisable(!this.activeButtonsWhenClickingCell);
        cellBackgroundPick.setDisable(!this.activeButtonsWhenClickingCell);
        createChartButton.setDisable(!this.activeButtonsWhenLoadingFile);
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

    public void buildNoNameRangePopup(ConfirmType Type) {
        Stage popupStage = new Stage();
        popupStage.setTitle("First, Provide the range!");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        TextField fromCellField = new TextField();
        fromCellField.setPromptText("From: e.g., A1");
        TextField toCellField = new TextField();
        toCellField.setPromptText("To: e.g., A6");
        setUnfocused(fromCellField, toCellField);
        Button confirmButton = createNewRangeButton(null, fromCellField, toCellField, popupStage, true, Type);
        vbox.getChildren().addAll(fromCellField, toCellField, confirmButton);
        Scene scene = new Scene(vbox, 300, 200);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality" + cssLoad + ".css")).toExternalForm());
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    public List<String> getAllColumns(String fromCell, String toCell) {
        List<String> columns = new ArrayList<>();
        for (int i = fromCell.charAt(0); i <= toCell.charAt(0); i++) {
            columns.add(String.valueOf((char) i));
        }
        return columns;
    }

    public void filterColumnPopup(Set<CellDataDTO> cells, String fromCell, String toCell) {
        Set<String> colToFilterBy = new HashSet<>();
        Stage popupStage = new Stage();
        popupStage.setTitle("Select Columns to Filter By:");
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        Label instructionLabel = new Label("Please select columns:");
        vbox.getChildren().add(instructionLabel);

        List<CheckBox> checkBoxes = new ArrayList<>();
        for (String column : getAllColumns(fromCell.toUpperCase(), toCell.toUpperCase())) {
            CheckBox checkBox = new CheckBox(column);
            checkBoxes.add(checkBox);
            vbox.getChildren().add(checkBox);
        }
        Button confirmButton = new Button("Confirm Selection");
        confirmButton.setOnAction(e -> {
            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    colToFilterBy.add(checkBox.getText());
                }
            }
            if (colToFilterBy.isEmpty()) {
                showInfoAlert("No columns selected");
                popupStage.toFront();
            } else {
                Map<String, Set<String>> columnToCellValues = getValuesFromCols(colToFilterBy, cells);
                confirmButton.setDisable(true);
                filterValuesPopup(fromCell, toCell, columnToCellValues);
                popupStage.close();
            }
        });

        vbox.getChildren().add(confirmButton);
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        popupStage.setScene(new Scene(scrollPane, 300, 300));
        popupStage.showAndWait();
    }

    private void filterValuesPopup(String fromCell, String toCell, Map<String, Set<String>> columnToCellValues) {
        if (columnToCellValues.isEmpty()) {
            showInfoAlert("There are no values in these column(s)");
            return;
        }
        Stage popupStage = new Stage();
        popupStage.setTitle("Select Cell Values");
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(30);
        gridPane.setVgap(10);
        Map<String, Set<String>> selectedValues = new HashMap<>();
        int columnIndex = 0;
        for (Map.Entry<String, Set<String>> entry : columnToCellValues.entrySet()) {
            String column = entry.getKey();
            Set<String> cellValues = entry.getValue();
            VBox columnVbox = new VBox(10);
            columnVbox.setAlignment(Pos.TOP_LEFT);
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

            gridPane.add(columnVbox, columnIndex++, 0);
        }
        RadioButton orFilter = new RadioButton("OR FILTERING");
        RadioButton andFilter = new RadioButton("AND FILTERING");
        ToggleGroup filterGroup = new ToggleGroup();
        orFilter.setToggleGroup(filterGroup);
        andFilter.setToggleGroup(filterGroup);
        orFilter.setSelected(true);
        HBox filterOptions = new HBox(10, orFilter, andFilter);
        filterOptions.setAlignment(Pos.CENTER);
        Button confirmButton = new Button("Confirm Selection");
        confirmButton.setOnAction(e -> {
            String selectedFilterType = orFilter.isSelected() ? "OR" : "AND";
            appController.filterParamsConfirmed(fromCell, toCell, selectedValues, selectedFilterType);
            popupStage.close();
        });
        VBox mainVbox = new VBox(20, gridPane, filterOptions, confirmButton);
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

    public void filterButtonListener() {
        appController.filterButtonClicked();
    }

    public void sortButtonListener() {
        appController.sortButtonClicked();
    }

    public void sortColumnPopup(String fromCell, String toCell) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Select columns to sort by:");

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        VBox choiceBoxContainer = new VBox(10);
        choiceBoxContainer.setAlignment(Pos.CENTER);

        int[] counter = {1};
        boolean[] isUpdating = {false};
        List<String> sortBy = new ArrayList<>();
        List<ChoiceBox<String>> choiceBoxes = new ArrayList<>();
        List<String> allColumns = getAllColumns(fromCell.toUpperCase(), toCell.toUpperCase());

        Runnable updateChoiceBoxOptions = () -> {
            isUpdating[0] = true;
            Set<String> selectedColumns = choiceBoxes.stream()
                    .map(ChoiceBox::getValue)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (ChoiceBox<String> choiceBox : choiceBoxes) {
                String currentSelection = choiceBox.getValue();
                choiceBox.getItems().clear();
                choiceBox.getItems().addAll(allColumns.stream()
                        .filter(col -> !selectedColumns.contains(col) || col.equals(currentSelection))
                        .toList());
                if (currentSelection != null) {
                    choiceBox.setValue(currentSelection);
                }
            }
            isUpdating[0] = false;
        };

        Runnable addNewChoiceBox = () -> {
            Label label = new Label("Enter column to sort by (priority " + counter[0]++ + ")");
            choiceBoxContainer.getChildren().add(label);
            ChoiceBox<String> choiceBox = new ChoiceBox<>();
            choiceBox.setOnAction(e -> {
                if (!isUpdating[0]) updateChoiceBoxOptions.run();
            });
            choiceBoxes.add(choiceBox);
            choiceBoxContainer.getChildren().add(choiceBox);
            updateChoiceBoxOptions.run();
        };

        Button addChoiceBoxButton = new Button("Add Another Column");
        addChoiceBoxButton.setOnAction(e -> {
            if (counter[0] <= allColumns.size()) addNewChoiceBox.run();
            else {
                addChoiceBoxButton.setText("No more columns...");
                addChoiceBoxButton.setDisable(true);
            }
        });
        choiceBoxContainer.getChildren().add(addChoiceBoxButton);

        Button confirmButton = new Button("Confirm Selection");
        confirmButton.setOnAction(e -> {
            for (ChoiceBox<String> choiceBox : choiceBoxes) {
                String selectedCol = choiceBox.getValue();
                if (selectedCol != null) sortBy.add(selectedCol);
            }
            appController.sortParamsConfirmed(fromCell, toCell, sortBy);
            popupStage.close();
        });

        vbox.getChildren().addAll(choiceBoxContainer, confirmButton);
        addNewChoiceBox.run();

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        popupStage.setScene(new Scene(scrollPane, 300, 400));
        popupStage.showAndWait();
    }

    private void buildColorPickerPopup(Consumer<Color> colorCallback) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Choose a Color");
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOnAction(e -> {
            try {
                Color selectedColor = colorPicker.getValue();
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
        inputField.setPromptText("Input new size here");
        setUnfocused(inputField);
        VBox vbox = createModifySizePopupVBox(inputField, popupStage, promptText, isColumn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 320, 150);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality" + cssLoad + ".css")).toExternalForm());
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
            if (newSize >= 20) {
                appController.updateSize(newSize);
                popupStage.close();
            } else {
                if (newSize <= 0)
                    showInfoAlert("Error: Entered 0 or a negative value.");
                else showInfoAlert("Error: Minimum size set to 20 for remaining functionality.");
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
        leftAlign.setToggleGroup(alignmentGroup);
        centerAlign.setToggleGroup(alignmentGroup);
        rightAlign.setToggleGroup(alignmentGroup);

        Button confirmButton = getButtonForAlimentPopup(alignmentGroup, popupStage);

        VBox vbox = new VBox(10, leftAlign, centerAlign, rightAlign, confirmButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        Scene scene = new Scene(vbox, 300, 150);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality" + cssLoad + ".css")).toExternalForm());
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
        buildColorPickerPopup((selectedColor) -> appController.textColorPicked(selectedColor));
    }

    @FXML
    private void cellBackgroundColorPick() {
        buildColorPickerPopup((selectedColor) -> appController.backgroundColorPicked(selectedColor));
    }

    @FXML
    public void resetCellStyleListener() {
        appController.resetStyleClicked();
    }

    private Button createNewRangeButton(TextField rangeNameField, TextField fromCellField, TextField toCellField, Stage currStage, boolean noName, ConfirmType type) {
        Button button = new Button("Confirm");
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
                    appController.noNameRangeSelected(fromCell, toCell, type);
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
        Button confirmButton = createNewRangeButton(rangeNameField, fromCellField, toCellField, popupStage, false, ConfirmType.ADD_NEW_RANGE);
        vbox.getChildren().addAll(rangeNameField, fromCellField, toCellField, confirmButton);

        Scene scene = new Scene(vbox, 300, 200);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality" + cssLoad + ".css")).toExternalForm());
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    private void setUnfocused(Node... nodes) {
        for (Node node : nodes) {
            node.setFocusTraversable(false);
        }
    }

    public void viewAndDeleteRangePopup(ConfirmType type, Set<String> rangeNames) throws IOException {
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
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/components/body/table/func/tableFunctionality" + cssLoad + ".css")).toExternalForm());
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    private Button createViewOrDeleteRangeButton(ConfirmType type, Stage currStage, ToggleGroup toggleGroup) {
        Button confirmButton = new Button("Confirm");
        confirmButton.getStyleClass().add("button");
        confirmButton.setOnAction(e -> {
            RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
            if (selectedRadioButton != null) {
                String selectedRangeName = selectedRadioButton.getText();
                if (type.equals(ConfirmType.VIEW_EXISTING_RANGE)) {
                    appController.showRangeConfirmedClicked(selectedRangeName);
                } else if (type.equals(ConfirmType.DELETE_EXISTING_RANGE)) {
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
        try {
            appController.deleteOrViewExistingRangeClicked(ConfirmType.VIEW_EXISTING_RANGE);
        }
        catch (Exception e) {
            showInfoAlert(e.getMessage());
        }
    }

    @FXML
    private void deleteExistingRangeListener() {
        try{
            appController.deleteOrViewExistingRangeClicked(ConfirmType.DELETE_EXISTING_RANGE);
        }
        catch (Exception e) {
            showInfoAlert(e.getMessage());
        }
    }

    public void createChartButtonListener() {
        appController.chartButtonClicked();
    }
}
