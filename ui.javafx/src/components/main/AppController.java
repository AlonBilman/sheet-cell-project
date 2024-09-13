package components.main;

import checkfile.STLSheet;
import components.body.table.func.TableFunctionalityController;
import components.body.table.view.GridSheetController;
import components.header.cellfunction.CellFunctionsController;
import components.header.loadfile.LoadFileController;
import components.header.title.TitleCardController;
import dto.CellDataDTO;
import dto.LoadDTO;
import dto.RangeDTO;
import dto.sheetDTO;
import engine.impl.EngineImpl;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static checkfile.CheckForXMLFile.loadXMLFile;

public class AppController {

    private Stage stage;
    private EngineImpl engine;
    private File newFile, oldFile;
    private String userInput;
    private STLSheet stlSheet;
    private sheetDTO sheetDto;
    private LoadDTO loadResult;
    private CellDataDTO cellData;

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
            //tableFunctionalityController.add version o mashu
        } catch (Exception e) {
            loadFileController.showInfoAlert(e.getMessage());
        }
    }

    public void CellClicked(String id) {
        cellOutOfFocus();
        boardOutOfFocus();
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
            cellOutOfFocus();
            boardOutOfFocus();
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
        cellOutOfFocus();
        boardOutOfFocus();
        cellFunctionsController.setFocus(boarderTextId);
        gridSheetController.focusOnDesiredCells(boarderTextId);
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
        String params = fromCell.trim() + ".." + toCell.trim();
        engine.addRange(rangeName, params);
    }

    private Set<String> getExistingRanges() {
        Set<String> rangeNames = engine.Display().getActiveRanges().keySet(); //get the names
        return rangeNames.stream()
                .sorted() //sort the names
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> viewExistingRangeClicked() {
        // need to focus the cells
        return getExistingRanges();
        //Do some logic

    }

    public Set<String> deleteRangeClicked() {
        return getExistingRanges();
        //Do some logic
    }

}

