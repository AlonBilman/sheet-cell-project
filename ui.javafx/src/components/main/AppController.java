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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static checkfile.CheckForXMLFile.loadXMLFile;

public class AppController {

    public enum Style {
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
    private Map<String, Set<String>> selectedValues = new HashMap<>();
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

    public void saveSelectedValues(Map<String, Set<String>> selectedValues) {
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
        engine.setBackgroundColor(id, selectedColor.toString());
        gridSheetController.changeBackgroundColor(id, selectedColor);
    }

    public void textColorPicked(Color selectedColor) {
        String id = cellFunctionsController.getCellIdFocused();
        engine.setTextColor(id, selectedColor.toString());
        gridSheetController.changeTextColor(id, selectedColor);
    }

    public void resetStyleClicked() {
        String id = cellFunctionsController.getCellIdFocused();
        gridSheetController.resetToDefault(id);
        engine.setTextColor(id, null);
        engine.setBackgroundColor(id, null);

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
            cellFunctionsController.showVersion(engine.getSheets().get(selectedVersion), selectedVersion.toString());
        } catch (Exception e) {
            cellFunctionsController.showInfoAlert(e.getMessage());
        }
    }

//-------------------------------------------------------------------------------------------------------------//

    public void noNameRangeSelected(String fromCell, String toCell) {
        String params = fromCell.trim() + ".." + toCell.trim();
        try {
            Set<CellDataDTO> cells = engine.getSetOfCellsDtoDummyRange(params);
            tableFunctionalityController.filterColumnPopup(cells, fromCell, toCell);
        } catch (RuntimeException e) {
            tableFunctionalityController.showInfoAlert(e.getMessage());
        }
    }

    public void filterButtonClicked() {
        tableFunctionalityController.buildNoNameRangePopup();
    }

    public void filterParamsConfirmed(String fromCell, String toCell, Map<String, Set<String>> filterBy) {
        sheetDTO filteredSheet = engine.filter(fromCell.trim() + ".." + toCell.trim(), filterBy);
        try {
            cellFunctionsController.showVersion(filteredSheet, "Filtered");
        }
        catch (IOException e) {
            tableFunctionalityController.showInfoAlert(e.getMessage());
        }
    }

//    public void sortButtonClicked() {
//        List<String> colToFilterBy = new ArrayList<>();
//        buildPopUpOfColsToSort(colToFilterBy);
//
//        try {
//            buildFilteredPopup(engine.sort(rangeParams, colToFilterBy), false);
//        } catch (Exception e) {
//            cellFunctionsController.showInfoAlert(e.getMessage());
//        }
//    }

//    public void buildPopUpOfColsToSort(List<String> colToFilterBy) {
//        int colNum = engine.Display().getColSize();
//
//        // Create a new Stage (popup window)
//        Stage popupStage = new Stage();
//        popupStage.setTitle("Select columns to sort by:");
//
//        // Create a VBox to hold the choice boxes and buttons
//        VBox vbox = new VBox();
//        vbox.setSpacing(20);
//        vbox.setAlignment(Pos.CENTER);
//
//        // TextField to input range
//        TextField rangeGetTextField = new TextField();
//        rangeGetTextField.setPromptText("Enter area to filter like A1..B2:");
//        rangeGetTextField.setMinWidth(250);
//        rangeGetTextField.setFocusTraversable(false);
//        vbox.getChildren().add(rangeGetTextField);
//
//
//        // List to store all ChoiceBoxes
//        List<ChoiceBox<String>> choiceBoxes = new ArrayList<>();
//
//        List<String> allColumns = new ArrayList<>();
//        for (int i = 1; i <= colNum; i++) {
//            allColumns.add(String.valueOf((char) ('A' + (i - 1))));
//        }
//
//        // Method to update available options in all ChoiceBoxes
//        Runnable updateChoiceBoxOptions = () -> {
//            Set<String> selectedColumns = choiceBoxes.stream()
//                    .map(ChoiceBox::getValue)
//                    .filter(Objects::nonNull) // Exclude null values (unselected boxes)
//                    .collect(Collectors.toSet());
//
//            for (ChoiceBox<String> choiceBox : choiceBoxes) {
//                String currentSelection = choiceBox.getValue();
//                choiceBox.getItems().clear(); // Clear existing items
//
//                // Add back all columns except the ones that are already selected
//                choiceBox.getItems().addAll(
//                        allColumns.stream()
//                                .filter(col -> !selectedColumns.contains(col) || col.equals(currentSelection))
//                                .toList()
//                );
//
//                // Preserve the current selection
//                if (currentSelection != null) {
//                    choiceBox.setValue(currentSelection);
//                }
//            }
//        };
//
//        // Method to add a new ChoiceBox
//        Runnable addNewChoiceBox = () -> {
//            Label label = new Label("Enter column to sort by:");
//            vbox.getChildren().add(label);
//            ChoiceBox<String> choiceBox = new ChoiceBox<>();
//            choiceBox.setOnAction(e -> updateChoiceBoxOptions.run());
//            choiceBox.getSelectionModel().select(0); // Select the first item as a placeholder
//            choiceBoxes.add(choiceBox);
//            vbox.getChildren().add(choiceBox); // Add it directly to the VBox
//            updateChoiceBoxOptions.run(); // Update available columns immediately after adding
//        };
//
//        // Button to add more ChoiceBoxes
//        Button addChoiceBoxButton = new Button("Add Another Column");
//        addChoiceBoxButton.setOnAction(e -> addNewChoiceBox.run());
//        vbox.getChildren().add(addChoiceBoxButton);
//
//        // Add a button to confirm the selection
//        Button confirmButton = new Button("Confirm Selection");
//        confirmButton.setOnAction(e -> {
//            colToFilterBy.clear(); // Clear existing selections
//            for (ChoiceBox<String> choiceBox : choiceBoxes) {
//                String selectedCol = choiceBox.getValue();
//                if (selectedCol != null) {
//                    colToFilterBy.add(selectedCol); // Add selected columns to the set
//                }
//            }
//            saveRangeParams(rangeGetTextField.getText());
//            popupStage.close();
//        });
//
//        vbox.getChildren().add(confirmButton);
//
//        // Add the first ChoiceBox
//        addNewChoiceBox.run();
//
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.setContent(vbox);
//
//        popupStage.setScene(new Scene(scrollPane, 300, 400));
//        popupStage.showAndWait();
//    }
//

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

    public void setStyleOnParts() {
        tableFunctionalityController.updateStyleOfVBox(getStyleChosen());
        loadFileController.updateLoadHBoxStyle(getStyleChosen());
        cellFunctionsController.updateCellHBoxStyle(getStyleChosen());
        gridSheetController.changeGridPaneStyle(getStyleChosen());

    }

    public void updateCells() {
        gridSheetController.updateAllCellStyles(getStyleChosen());
    }

    public Style getStyleChosen() {
        return styleChosen;
    }

}