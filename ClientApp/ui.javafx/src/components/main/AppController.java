package components.main;

import com.google.gson.Gson;
import components.body.table.func.TableFunctionalityController;
import components.body.table.view.GridSheetController;
import components.header.cellfunction.CellFunctionsController;
import components.header.loadfile.LoadFileController;
import components.header.title.TitleCardController;
import dto.CellDataDTO;
import dto.sheetDTO;
import http.HttpClientUtil;
import javafx.application.Platform;
import manager.impl.SheetManagerImpl;
import expression.api.ObjType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static constants.Constants.BASE_DIRECTORY;
import static constants.Constants.LOADFILE;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

public class AppController {

    private ChartMaker chartMaker;
    private SheetManagerImpl engine;

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
    private Gson gson;

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
            chartMaker = new ChartMaker(this);
            engine = new SheetManagerImpl();
            gson = new Gson();
        }
    }

    private void disableComponents(boolean disable) {
        titleCard.setDisable(disable);
        cellFunctions.setDisable(disable);
        tableFunctionality.setDisable(disable);
        loadFile.setDisable(disable);
        gridSheet.setDisable(disable);
    }

    private void outOfFocus() {
        cellOutOfFocus();
        boardOutOfFocus();
    }

    private void loadFileLogic(File file) throws IOException {
        disableComponents(false);

        // Validate the file
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File does not exist or is not a valid file.");
        }

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM) // Set the multipart type
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("text/xml")))
                .build();

        String finalURL = BASE_DIRECTORY + LOADFILE;

        HttpClientUtil.runAsyncPost(finalURL, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace(); // Print the stack trace for better debugging
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code()==SC_OK) {
                    System.out.println("File uploaded successfully.");

                } else {

                    // Use Platform.runLater to ensure the UI updates happen on the JavaFX Application Thread
                    Platform.runLater(() -> {
                        loadFileController.showInfoAlert("Invalid file." );
                    });

                    System.out.println(response.code());
                }
            }
        });

        //data = file.data
        //send http request to the server with data.
        //post + body as InputStream
        // wait for response
        //if 200 = > continue
        //else => showInfoAlert


//        loadResult = engine.Load(newFile);
//        if (loadResult.isNotValid()) {
//            if (!engine.containSheet()) {
//                loadFileController.showInfoAlert("Invalid file. Ensure it exists and it is an XML file.");
//            } else if (oldFile != null) {
//                loadFileController.showInfoAlert("Invalid file. The previous file is retained.");
//                loadResult = engine.Load(oldFile);
//            }
//        } else {
//            oldFile = newFile;
//        }
//        stlSheet = loadXMLFile(loadResult.getLoadedFile());
//        try {
//            engine.initSheet(stlSheet);
//            gridSheetController.populateTableView(engine.Display(), true);
//            loadFileController.editFilePath(file.getAbsolutePath());
//            tableFunctionalityController.setActiveButtons
//                    (TableFunctionalityController.ButtonState.LOADING_FILE, true);
//            cellFunctionsController.wakeVersionButton();
//        } catch (Exception e) {
//            loadFileController.showInfoAlert(e.getMessage());
//        }
//    }
    }

    public void checkAndLoadFile(File file) {
        disableComponents(true);
        loadFileController.taskLoadingSimulation(() -> {
            try {
                loadFileLogic(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void CellClicked(String id) {
        outOfFocus();
        CellDataDTO cell = engine.showCell(id);
        cellFunctionsController.showCell(cell);
        tableFunctionalityController.setActiveButtons(
                TableFunctionalityController.ButtonState.CLICKING_CELL, true);
        gridSheetController.colorizeImportantCells(engine.Display(), id);
        if (cell.getEffectiveValue().getObjType().equals(ObjType.NUMERIC))
            cellFunctionsController.showNumericButtons(true);
    }

    public void loadClicked() {
        cellFunctionsController.outOfFocus();
        tableFunctionalityController.outOfFocus();
    }

    public void updateCellClicked(String cellToUpdate, String newOriginalValue) {
        try {
            engine.updateCell(cellToUpdate, newOriginalValue, false);
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
        gridSheetController.updateAliment(alignment);
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
            cellFunctionsController.showVersion(
                    engine.getSheets().get(selectedVersion), "Version Number: " + selectedVersion.toString());
        } catch (Exception e) {
            cellFunctionsController.showInfoAlert(e.getMessage());
        }
    }
    //-------------------------------------------------------------------------------------------------------------//

    public void noNameRangeSelected(String fromCell, String toCell, TableFunctionalityController.ConfirmType type) {
        String params = fromCell.trim() + ".." + toCell.trim();
        try {
            //has to be done for both filter and sort because if the range contains cells that we did not create we have to create these cells
            Set<CellDataDTO> cells = engine.getSetOfCellsDtoDummyRange(params);
            if (type.equals(TableFunctionalityController.ConfirmType.FILTER_RANGE))
                tableFunctionalityController.filterColumnPopup(cells, fromCell, toCell);
            else tableFunctionalityController.sortColumnPopup(fromCell.toUpperCase(), toCell.toUpperCase());
        } catch (RuntimeException e) {
            tableFunctionalityController.showInfoAlert(e.getMessage());
        }
    }

    public void filterButtonClicked() {
        tableFunctionalityController.buildNoNameRangePopup(TableFunctionalityController.ConfirmType.FILTER_RANGE);
    }

    public void sortButtonClicked() {
        tableFunctionalityController.buildNoNameRangePopup(TableFunctionalityController.ConfirmType.SORT_RANGE);
    }

    public void filterParamsConfirmed(String fromCell, String toCell, Map<String, Set<String>> filterBy, String selectedFilterType) {
        sheetDTO filteredSheet;
        String params = fromCell.trim() + ".." + toCell.trim();
        if (selectedFilterType.equals("OR"))
            filteredSheet = engine.filter(params, filterBy, SheetManagerImpl.OperatorValue.OR_OPERATOR);
        else {
            filteredSheet = engine.filter(params, filterBy, SheetManagerImpl.OperatorValue.AND_OPERATOR);
        }
        try {
            showSheetPopup(filteredSheet,
                    "Filtered from " + fromCell + " to " + toCell + " | By : " + filterBy);
        } catch (IOException e) {
            tableFunctionalityController.showInfoAlert(e.getMessage());
        }
    }

    public void sortParamsConfirmed(String fromCell, String toCell, List<String> sortBy) {
        sheetDTO sortedSheet = engine.sort(fromCell.trim() + ".." + toCell.trim(), sortBy);
        try {
            showSheetPopup(sortedSheet,
                    "Sorted from " + fromCell + " to " + toCell + " | By : " + sortBy);
        } catch (IOException e) {
            tableFunctionalityController.showInfoAlert(e.getMessage());
        }
    }

    public void showSheetPopup(sheetDTO sheet, String title) throws IOException {
        Stage stage = new Stage();
        stage.setTitle(title);
        FXMLLoader loader = new FXMLLoader();
        URL versionFXML = getClass().getResource("/components/body/table/view/gridSheetView.fxml");
        loader.setLocation(versionFXML);
        Parent root = loader.load();
        GridSheetController controller = loader.getController();
        controller.setMainController(this);
        controller.populateTableView(sheet, true);
        controller.disableGridPane();
        Scene scene = new Scene(root, 800, 800);
        stage.setScene(scene);
        stage.show();
    }

    public void setStyleOnParts(String value) {
        if (value.equals("Theme 1"))
            setNewTheme("Theme1");
        else if (value.equals("Theme 2"))
            setNewTheme("Theme2");
        else
            setNewTheme("Default");
    }

    private void setNewTheme(String value) {
        tableFunctionalityController.setTheme(value);
        loadFileController.setTheme(value);
        cellFunctionsController.setTheme(value);
        gridSheetController.setTheme(value);
        titleCardController.setTheme(value);
    }

    public void dynamicChangeButtonClicked() {
        disableComponents(true);
        cellFunctions.setDisable(false);
        gridSheet.opacityProperty().setValue(1);
        cellFunctionsController.setDynamicFuncDisable(true);
        engine.saveCellValue(cellFunctionsController.getCellIdFocused());
    }

    public void updateCellDynamically(String cellId, String newOriginalValue) {
        sheetDTO dynamicDto = engine.setOriginalValDynamically(cellId, newOriginalValue);
        gridSheetController.populateTableView(dynamicDto, false);
    }

    public void dynamicCancelClicked() {
        cellFunctionsController.exitDynamicChange();
    }

    public void exitDynamicChangeClicked() {
        disableComponents(false);
        cellFunctionsController.setDynamicFuncDisable(false);
        sheetDTO oldDto = engine.finishedDynamicallyChangeFeature(cellFunctionsController.getCellIdFocused());
        gridSheetController.populateTableView(oldDto, false);
        cellFunctionsController.showCell(
                oldDto.getActiveCells().get(cellFunctionsController.getCellIdFocused()));
    }

    public void chartButtonClicked() {
        chartMaker.createChartDialogPopup(engine.Display().getColSize());
    }

    public void confirmChartClicked(String chartType, String paramsX, String paramsY) {
        try {
            Set<CellDataDTO> xCells = engine.getSetOfCellsDtoDummyRange(paramsX);
            Set<CellDataDTO> yCells = engine.getSetOfCellsDtoDummyRange(paramsY);
            List<Double> xValues = getNumericValuesSortedByRow(xCells);
            List<Double> yValues = getNumericValuesSortedByRow(yCells);
            if (chartType.equals("Bar Chart"))
                chartMaker.createBarChart(xValues, yValues);
            else {
                chartMaker.createLineChart(xValues, yValues);
            }
        } catch (IllegalArgumentException e) {
            chartMaker.showInfoAlert(e.getMessage());
        }
    }

    //because I used set in so much code...
    private List<Double> getNumericValuesSortedByRow(Set<CellDataDTO> cells) {
        return cells.stream()
                .filter(cell -> cell.getEffectiveValue().getObjType() == ObjType.NUMERIC)
                .sorted(Comparator.comparingInt(CellDataDTO::getRow))
                .map(cell -> (double) cell.getEffectiveValue().getValue())
                .collect(Collectors.toList());
    }

}
