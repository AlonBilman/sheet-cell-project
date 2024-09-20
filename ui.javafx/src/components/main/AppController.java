package components.main;

import checkfile.STLSheet;
import components.body.table.func.TableFunctionalityController;
import components.body.table.view.GridSheetController;
import components.header.cellfunction.CellFunctionsController;
import components.header.loadfile.LoadFileController;
import components.header.title.TitleCardController;
import dto.CellDataDTO;
import dto.LoadDTO;
import dto.sheetDTO;
import engine.impl.EngineImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static checkfile.CheckForXMLFile.loadXMLFile;

public class AppController {


    public enum Style{
        DEFAULT_STYLE,
        DARK_MODE
    }


    private Stage stage;
    private EngineImpl engine;
    private File newFile, oldFile;
    private String userInput;
    private STLSheet stlSheet;
    private sheetDTO sheetDto;
    private LoadDTO loadResult;
    private CellDataDTO cellData;
    public boolean isStyleChanged = false;
    private Map<String,Set<String>> selectedValues = new HashMap<>();
    private String rangeParams;

    //all the components
    @FXML
    private AnchorPane titleCard;
    @FXML
    private HBox cellFunctions;
    @FXML
    private ScrollPane tableFunctionality;
    @FXML
    private HBox loadFile;
    @FXML
    private ScrollPane gridSheet;

    //controllers
    @FXML
    private TableFunctionalityController tableFunctionalityController;
    @FXML
    private GridSheetController gridSheetController;
    @FXML
    private CellFunctionsController cellFunctionsController;
    @FXML
    private LoadFileController loadFileController;
    @FXML
    private TitleCardController titleCardController;

    public void setTableFunctionalityController(TableFunctionalityController tableFunctionalityController) {
        this.tableFunctionalityController = tableFunctionalityController;
    }

    public void setGridSheetController(GridSheetController gridSheetController) {
        this.gridSheetController = gridSheetController;
    }

    public void setCellFunctionsController(CellFunctionsController cellFunctionsController) {
        this.cellFunctionsController = cellFunctionsController;
    }

    public void setLoadFileController(LoadFileController loadFileController) {
        this.loadFileController = loadFileController;
    }

    public void setTitleCardController(TitleCardController titleCardController) {
        this.titleCardController = titleCardController;
    }

    public void initialize() {
        if (tableFunctionalityController != null && gridSheetController != null &&
                cellFunctionsController != null && loadFileController != null && titleCardController != null) {
            tableFunctionalityController.setMainController(this);
            cellFunctionsController.setMainController(this);
            loadFileController.setMainController(this);
            titleCardController.setMainController(this);
            gridSheetController.setMainController(this);
            engine = new EngineImpl();
        }
    }

    public void saveSelectedValues(Map<String,Set<String>> selectedValues) {
        // Process or save the selected values as needed
        this.selectedValues = selectedValues;
    }

    private void outOfFocus() {
        cellOutOfFocus();
        boardOutOfFocus();
    }

    public void checkAndLoadFile(File file) {
        newFile = file;
        loadResult = engine.Load(newFile);
        if (loadResult.isNotValid()) {
            if (!engine.containSheet()) {
                loadFileController.showInfoAlert("Invalid file. Ensure it exists and it is an XML file.");
            } else if (oldFile != null) {
                loadFileController.showInfoAlert("Invalid file. The previous file is retained.");
                loadResult = engine.Load(oldFile);
            }
        } else {
            oldFile = newFile;
        }
        stlSheet = loadXMLFile(loadResult.getLoadedFile());
        try {
            engine.initSheet(stlSheet);
            gridSheetController.populateTableView(engine.Display(), true);
            loadFileController.editFilePath(file.getAbsolutePath());
            tableFunctionalityController.setActiveButtons
                    (TableFunctionalityController.ButtonState.LOADING_FILE, true);
            cellFunctionsController.wakeVersionButton();
        } catch (Exception e) {
            loadFileController.showInfoAlert(e.getMessage());
        }
    }


    public void CellClicked(String id) {
        outOfFocus();
        CellDataDTO cell = engine.showCell(id);
        cellFunctionsController.showCell(cell);
        tableFunctionalityController.setActiveButtons(
                TableFunctionalityController.ButtonState.CLICKING_CELL, true);
        gridSheetController.colorizeImportantCells(engine.Display(), id);
    }

    public void loadClicked() {
        cellFunctionsController.outOfFocus();
        tableFunctionalityController.outOfFocus();
    }

    public void updateCellClicked(String cellToUpdate, String newOriginalValue) {
        try {
            engine.updateCell(cellToUpdate, newOriginalValue);
            outOfFocus();
            gridSheetController.populateTableView(engine.Display(), false);
        } catch (Exception e) {
            cellFunctionsController.showInfoAlert(e.getMessage());
        }
        //can fail. thrown back to the caller
    }

    public void cellOutOfFocus() {
        cellFunctionsController.outOfFocus();
        gridSheetController.returnOldColors();
        tableFunctionalityController.setActiveButtons(
                TableFunctionalityController.ButtonState.CLICKING_CELL, false);
    }


    public void backgroundColorPicked(Color selectedColor) {
        String id = cellFunctionsController.getCellIdFocused();
        gridSheetController.changeBackgroundColor(id, selectedColor);
    }

    public void textColorPicked(Color selectedColor) {
        String id = cellFunctionsController.getCellIdFocused();
        gridSheetController.changeTextColor(id, selectedColor);
    }

    public void resetStyleClicked() {
        String id = cellFunctionsController.getCellIdFocused();
        gridSheetController.resetToDefault(id);

    }

    public void BoarderClicked(String boarderTextId) {
        outOfFocus();
        cellFunctionsController.setFocus(boarderTextId);
        gridSheetController.focusOnBorderAbilityCells(boarderTextId);
        if (Character.isDigit(boarderTextId.charAt(0)))
            tableFunctionalityController.setActiveButtons(TableFunctionalityController.ButtonState.CLICKING_ROW, true);
        else
            tableFunctionalityController.setActiveButtons(TableFunctionalityController.ButtonState.CLICKING_COLUMN, true);
    }

    public void boardOutOfFocus() {
        gridSheetController.returnOldColors();
        tableFunctionalityController.setActiveButtons(TableFunctionalityController.ButtonState.CLICKING_ROW, false);
        tableFunctionalityController.setActiveButtons(TableFunctionalityController.ButtonState.CLICKING_COLUMN, false);
    }

    public void updateSize(double inputField) {
        String id = cellFunctionsController.getCellIdFocused();
        gridSheetController.updateSize(inputField, id);
    }

    public void setAlignment(String alignment) {
        String id = cellFunctionsController.getCellIdFocused();
        gridSheetController.updateAliment(alignment, id);
    }

    public void addNewRange(String rangeName, String fromCell, String toCell) {
        outOfFocus();
        String params = fromCell.trim() + ".." + toCell.trim();
        engine.addRange(rangeName, params);
    }

    public Set<String> getExistingRanges() {
        Set<String> rangeNames = engine.Display().getActiveRanges().keySet(); //get the names
        return rangeNames.stream()
                .sorted() //sort the names
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void deleteRangeConfirmedClicked(String rangeToDelete) {
        outOfFocus();
        try {
            engine.deleteRange(rangeToDelete);
        } catch (Exception e) {
            tableFunctionalityController.showInfoAlert(e.getMessage());
        }
    }

    public void showRangeConfirmedClicked(String selectedRangeName) {
        outOfFocus();
        Set<String> labelNamesToFocus = new HashSet<>();
        Set<CellDataDTO> focusCells =
                engine.Display()
                        .getActiveRanges()
                        .get(selectedRangeName)
                        .getCells();
        for (CellDataDTO focusCell : focusCells) {
            labelNamesToFocus.add(focusCell.getId());
        }
        gridSheetController.focusOnRangeCells(labelNamesToFocus);
    }

    public void getVersionClicked() {
        cellFunctionsController.buildVersionPopup(engine.Display().getSheetVersionNumber());
    }

    public void confirmVersionClicked(Integer selectedVersion) {
        try {
            cellFunctionsController.showVersion(engine.getSheets().get(selectedVersion),selectedVersion);
        }
        catch (Exception e) {
            cellFunctionsController.showInfoAlert(e.getMessage());
        }
    }


    // Method to build the popup for column selection
    public void buildPopUpOfCols(Set<String> colToFilterBy) {
        int colNum = engine.Display().getColSize();
        // Create a new Stage (popup window)
        Stage popupStage = new Stage();
        popupStage.setTitle("Select columns to filter by:");

        // Create a VBox to hold the checkboxes
        VBox vbox = new VBox();
        vbox.setSpacing(20);
        vbox.setAlignment(Pos.CENTER);
        TextField rangeGetTextField = new TextField();
        rangeGetTextField.setPromptText("Enter area to filter like A1..B2:");
        //Add check of valid range
        rangeGetTextField.setMinWidth(250);
        rangeGetTextField.setFocusTraversable(false);
        vbox.getChildren().add(rangeGetTextField);

        List<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 1; i <= colNum; i++) {
            // Adjust column label to start from 'A'
            CheckBox checkBox = new CheckBox(String.valueOf((char) ('A' + (i - 1))));
            checkBoxes.add(checkBox);
            vbox.getChildren().add(checkBox);
        }

        // Add a button to confirm the selection
        Button confirmButton = new Button("Confirm Selection");
        confirmButton.setOnAction(e -> {
            colToFilterBy.clear();  // Clear existing selections
            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    colToFilterBy.add(checkBox.getText());
                }
            }
            saveRangeParams(rangeGetTextField.getText());
            popupStage.close();
        });

        vbox.getChildren().add(confirmButton);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        popupStage.setScene(new Scene(scrollPane, 300, 300));
        popupStage.showAndWait();
    }

    private void saveRangeParams(String text) {
        this.rangeParams = text;
    }

    private Label getNodeFromGridPane(GridPane gridPane, String cellPosition) {
        // Assuming the gridPane's children are organized in a specific layout that we can identify by position
        for (Node node : gridPane.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex != null && rowIndex != null) {
                // Assuming the cellPosition format is "A1", "B3", etc.
                String columnLetter = String.valueOf((char) ('A' + colIndex - 1));
                String expectedPosition = columnLetter + rowIndex;
                if (expectedPosition.equals(cellPosition) && node instanceof Label) {
                    return (Label) node;
                }
            }
        }
        return null;
    }

    private Map<String, Set<String>> getValuesFromCols(Set<String> colToFilterBy, GridPane gridPane, int rows) {
        Map<String, Set<String>> columnToCellValues = new HashMap<>();

        for (String col : colToFilterBy) {
            int colIndex = col.charAt(0) - 'A';  // Convert column letter to zero-based index

            Set<String> cellValues = new HashSet<>();

            for (int rowIndex = 1; rowIndex <= rows; rowIndex++) {
                Label cell = getNodeFromGridPane(gridPane, String.valueOf((char) ('A' + colIndex)) + rowIndex);

                if (cell != null) {
                    String cellValue = cell.getText();
                    if (cellValue != null && !cellValue.trim().isEmpty()) {
                        cellValues.add(cellValue);
                    }
                }
            }

            columnToCellValues.put(col, cellValues);
        }
        return columnToCellValues;
    }

    // Method to display the popup with cell values for selection
    private void showPopupWithCellValues(Map<String, Set<String>> columnToCellValues) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Select Cell Values");

        HBox hbox = new HBox();
        hbox.setSpacing(30);
        hbox.setAlignment(Pos.CENTER);

        Map<String, Set<String>> selectedValues = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : columnToCellValues.entrySet()) {
            String column = entry.getKey();  // Column label should be correct
            Set<String> cellValues = entry.getValue();

            VBox columnVbox = new VBox();
            columnVbox.setSpacing(10);
            columnVbox.setAlignment(Pos.CENTER_LEFT);

            Label columnLabel = new Label("Column: " + column);
            columnVbox.getChildren().add(columnLabel);

            Set<String> selectedColumnValues = new HashSet<>();
            selectedValues.put(column, selectedColumnValues);

            for (String value : cellValues) {
                if (!Objects.equals(value, "")) {
                    CheckBox checkBox = new CheckBox(value);

                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            selectedColumnValues.add(value);
                        } else {
                            selectedColumnValues.remove(value);
                        }
                    });

                    columnVbox.getChildren().add(checkBox);
                }
            }

            hbox.getChildren().add(columnVbox);
        }

        Button confirmButton = new Button("Confirm Selection");
        confirmButton.setOnAction(e -> {
            System.out.println("Selected values per column:");
            selectedValues.forEach((column, values) -> {
                System.out.println("Column: " + column + " -> Selected Values: " + values);
            });
            saveSelectedValues(selectedValues);
            popupStage.close();
        });

        VBox mainVbox = new VBox();
        mainVbox.setSpacing(20);
        mainVbox.setAlignment(Pos.CENTER);
        mainVbox.getChildren().addAll(hbox, confirmButton);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainVbox);
        scrollPane.setFitToWidth(true);

        Scene popupScene = new Scene(scrollPane, 500, 400);
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    public void sortButtonClicked() throws IOException {
        Set<String> colToFilterBy = new HashSet<>();
        buildPopUpOfCols(colToFilterBy);

        buildFilteredPopup(engine.sort(rangeParams,colToFilterBy.stream().toList()),false);
    }

    public void filterButtonClicked() {
        Set<String> colToFilterBy = new HashSet<>();
        buildPopUpOfCols(colToFilterBy);

        // Get the cell values based on the columns to filter by
        Map<String, Set<String>> cellValues = getValuesFromCols(colToFilterBy, gridSheetController.getGridPane(), engine.Display().getRowSize());

        // Show the popup with the cell values for selection
        if (!cellValues.isEmpty()) {
            showPopupWithCellValues(cellValues);
        }

        if (!selectedValues.isEmpty()) {
            Platform.runLater(() -> {
                try {
                    buildFilteredPopup(engine.filter(rangeParams, selectedValues),true);
                } catch (IOException e) {
                    cellFunctionsController.showInfoAlert(e.getMessage());
                }
            });
        }
    }

    private void buildFilteredPopup(sheetDTO filter, boolean isFilter) throws IOException {
        Stage stage = new Stage();
        if(isFilter) {
            stage.setTitle("Filtered Sheet");
        }
        else {
            stage.setTitle("Sorted Sheet");
        }
        FXMLLoader loader = new FXMLLoader();
        URL versionFXML = getClass().getResource("/components/body/table/view/gridSheetView.fxml");
        loader.setLocation(versionFXML);
        Parent root = loader.load();

        GridSheetController controller = loader.getController();
        controller.setMainController(this);
        controller.populateTableView(filter, true);
        controller.disableGridPane();


        Scene scene = new Scene(root, 800, 800);
        stage.setScene(scene);
        stage.showAndWait();
    }


    private Style styleChosen = Style.DEFAULT_STYLE;

    public void setStyleChosen(String styleName) {
        switch (styleName) {
            case "No style":
                styleChosen = Style.DEFAULT_STYLE;
                break;
            case "Dark theme":
                styleChosen = Style.DARK_MODE;
                break;
        }
    }

    public void setStyleOnParts(){
        tableFunctionalityController.updateStyleOfVBox(getStyleChosen());
        loadFileController.updateLoadHBoxStyle(getStyleChosen());
        cellFunctionsController.updateCellHBoxStyle(getStyleChosen());
        gridSheetController.changeGridPaneStyle(getStyleChosen());

    }
    public void updateCells(){
        gridSheetController.updateAllCellStyles(getStyleChosen());
    }

    public Style getStyleChosen() {
        return styleChosen;
    }

}