package components.page.view.sheetscreen;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import components.body.table.func.TableFunctionalityController;
import components.body.table.view.GridSheetController;
import components.header.cellfunction.CellFunctionsController;
import components.header.title.TitleCardController;
import components.page.view.mainscreen.MainScreenController;

import http.dto.CellDataDtoResp;
import http.dto.ObjType;
import http.dto.RangeDtoResp;
import http.dto.SheetDtoResp;
import http.utils.CallerService;
import http.utils.HttpClientUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static constants.Constants.*;

public class AppController {

    private ChartMaker chartMaker;
    private CallerService httpCallerService;
    private Map<String, String> query;
    private String currSheet;
    private Stage stage;
    private String userName;
    private SheetDtoResp currSheetDtoResp;
    private String theme;
    @FXML
    private TableFunctionalityController tableFunctionalityController;
    @FXML
    private GridSheetController gridSheetController;
    @FXML
    private CellFunctionsController cellFunctionsController;
    @FXML
    private TitleCardController titleCardController;

    @FXML
    private AnchorPane titleCard;
    @FXML
    private HBox cellFunctions;
    @FXML
    private ScrollPane tableFunctionality;
    @FXML
    private ScrollPane gridSheet;

    public void setTableFunctionalityController(TableFunctionalityController tableFunctionalityController) {
        this.tableFunctionalityController = tableFunctionalityController;
    }

    public void setGridSheetController(GridSheetController gridSheetController) {
        this.gridSheetController = gridSheetController;
    }

    public void setCellFunctionsController(CellFunctionsController cellFunctionsController) {
        this.cellFunctionsController = cellFunctionsController;
    }

    public void setTitleCardController(TitleCardController titleCardController) {
        this.titleCardController = titleCardController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        if (tableFunctionalityController != null && gridSheetController != null &&
                cellFunctionsController != null && titleCardController != null) {
            tableFunctionalityController.setMainController(this);
            cellFunctionsController.setMainController(this);
            titleCardController.setMainController(this);
            gridSheetController.setMainController(this);
            chartMaker = new ChartMaker(this);
            httpCallerService = new CallerService();
            query = new HashMap<>();
            currSheetDtoResp = null;
            theme = "Default";
        }
    }

    public void setReaderAccess() {
        cellFunctionsController.setReaderAccess();
        tableFunctionalityController.setReaderAccess();

    }

    public void updateSheetDtoVersion() {
        setSheet(currSheet, e -> Platform.runLater(() -> cellFunctionsController.showInfoAlert("Failed to update sheet version: " + e.getMessage())), false);
        Platform.runLater(this::outOfFocus);
    }

    public void setSheet(String sheetName, Consumer<Exception> error, boolean isLoad) {
        currSheet = sheetName;
        query.clear();
        query.put(SHEET_ID, currSheet);

        httpCallerService.fetchSheetAsync(query, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    currSheetDtoResp = GSON.fromJson(response.body().string(), SheetDtoResp.class);

                    Platform.runLater(() -> updateUIAfterSheetFetch(isLoad));
                } catch (Exception e) {
                    error.accept(e);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                error.accept(e);
            }
        });
    }

    private void updateUIAfterSheetFetch(boolean isLoad) {
        gridSheetController.populateTableView(currSheetDtoResp, isLoad);
        cellFunctionsController.wakeVersionButton();
        tableFunctionalityController.setActiveButtons(
                TableFunctionalityController.ButtonState.LOADING_FILE, true);
        query.clear();
        query.put(SHEET_ID, currSheet);
        titleCardController.startVersionRefresher(query, currSheetDtoResp.getSheetVersionNumber());//will stop the old one too.
    }

    private void disableComponents(boolean disable) {
        titleCard.setDisable(disable);
        cellFunctions.setDisable(disable);
        tableFunctionality.setDisable(disable);
        gridSheet.setDisable(disable);
    }

    private void outOfFocus() {
        cellOutOfFocus();
        boardOutOfFocus();
    }

    public void CellClicked(String id) {
        outOfFocus();
        CellDataDtoResp cell = currSheetDtoResp.getActiveCells().get(id);
        if (cell != null) {
            cellFunctionsController.showCell(cell);
            tableFunctionalityController.setActiveButtons(
                    TableFunctionalityController.ButtonState.CLICKING_CELL, true);
            gridSheetController.colorizeImportantCells(currSheetDtoResp, id);
            if (cell.getEffectiveValue().getObjType().equals(ObjType.NUMERIC)) {
                cellFunctionsController.showNumericButtons(true);
            }
        } else {
            cellFunctionsController.showEmptyCell(id);
            tableFunctionalityController.setActiveButtons(
                    TableFunctionalityController.ButtonState.CLICKING_CELL, true);
            gridSheetController.colorizeEmptyCell(id);
        }
    }

    public void updateCellClicked(String cellToUpdate, String newOriginalValue) {
        query.clear();
        query.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, cellToUpdate));

        httpCallerService.changeCellAsync(query, newOriginalValue, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> cellFunctionsController.showInfoAlert(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    updateSheetDtoVersion();
                } catch (Exception e) {
                    Platform.runLater(() -> cellFunctionsController.showInfoAlert(e.getMessage()));
                }
            }
        });
    }

    public void cellOutOfFocus() {
        cellFunctionsController.outOfFocus();
        gridSheetController.returnOldColors();
        tableFunctionalityController.setActiveButtons(
                TableFunctionalityController.ButtonState.CLICKING_CELL, false);
    }

    public void backgroundColorPicked(Color selectedColor) {
        String id = cellFunctionsController.getCellIdFocused();
        query.clear();
        query.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, id));
        String color;
        if (selectedColor != null)
            color = selectedColor.toString();
        else {
            color = null;
        }
        httpCallerService.changeColorAsync(query, CELL_BACKGROUND_COLOR, color, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    cellFunctionsController.showInfoAlert(e.getMessage());
                    outOfFocus();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    Platform.runLater(() -> {
                        if (color != null)
                            gridSheetController.changeBackgroundColor(id, selectedColor);
                        outOfFocus();

                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        cellFunctionsController.showInfoAlert(e.getMessage());
                        outOfFocus();
                    });
                }
            }
        });
    }

    public void textColorPicked(Color selectedColor) {
        String id = cellFunctionsController.getCellIdFocused();
        query.clear();
        query.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, id));
        String color;
        if (selectedColor != null)
            color = selectedColor.toString();
        else {
            color = null;
        }
        httpCallerService.changeColorAsync(query, CELL_TEXT_COLOR, color, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    cellFunctionsController.showInfoAlert(e.getMessage());
                    outOfFocus();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    Platform.runLater(() -> {
                        if (color != null)
                            gridSheetController.changeTextColor(id, selectedColor);
                        outOfFocus();
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        cellFunctionsController.showInfoAlert(e.getMessage());
                        outOfFocus();
                    });
                }
            }
        });
    }

    public void resetStyleClicked() {
        textColorPicked(null);
        backgroundColorPicked(null);
        Platform.runLater(() -> {
            String id = cellFunctionsController.getCellIdFocused();
            gridSheetController.resetToDefault(id);
        });
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
        HttpClientUtil.RangeBody rangeBody = new HttpClientUtil.RangeBody(rangeName, params);
        query.clear();
        query.put(SHEET_ID, currSheet);
        httpCallerService.addRange(query, rangeBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    RangeDtoResp rangeDto = new Gson().fromJson(response.body().string(), RangeDtoResp.class);
                    currSheetDtoResp.getActiveRanges().put(rangeName, rangeDto);
                } catch (Exception e) {
                    Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
                }
            }
        });
    }

    public void deleteOrViewExistingRangeClicked(TableFunctionalityController.ConfirmType type) {
        Set<String> rangeNames = currSheetDtoResp.getActiveRanges().keySet(); // get the names
        rangeNames = rangeNames.stream()
                .sorted() // sort the names
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> finalRangeNames = rangeNames;
        if (type.equals(TableFunctionalityController.ConfirmType.VIEW_EXISTING_RANGE)) {
            try {
                tableFunctionalityController.viewAndDeleteRangePopup(
                        TableFunctionalityController.ConfirmType.VIEW_EXISTING_RANGE, finalRangeNames);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                tableFunctionalityController.viewAndDeleteRangePopup(
                        TableFunctionalityController.ConfirmType.DELETE_EXISTING_RANGE, finalRangeNames);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteRangeConfirmedClicked(String rangeToDelete) {
        outOfFocus();
        query.clear();
        query.put(SHEET_ID, currSheet);
        HttpClientUtil.RangeBody range = new HttpClientUtil.RangeBody(rangeToDelete, "");

        httpCallerService.deleteRange(query, range, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    currSheetDtoResp.getActiveRanges().remove(rangeToDelete);
                } catch (Exception e) {
                    Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
            }
        });
    }

    public void showRangeConfirmedClicked(String selectedRangeName) {
        outOfFocus();
        Set<String> labelNamesToFocus = new HashSet<>();
        Set<CellDataDtoResp> focusCells = currSheetDtoResp.getActiveRanges()
                .get(selectedRangeName)
                .getCells();
        for (CellDataDtoResp focusCell : focusCells) {
            labelNamesToFocus.add(focusCell.getId());
        }
        gridSheetController.focusOnRangeCells(labelNamesToFocus);
    }

    public void getVersionClicked() {
        cellFunctionsController.buildVersionPopup(currSheetDtoResp.getSheetVersionNumber());
    }

    public void confirmVersionClicked(Integer selectedVersion) {
        query.clear();
        query.put(SHEET_ID, currSheet);
        httpCallerService.fetchSheetsAsync(query, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> cellFunctionsController.showInfoAlert(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    HttpClientUtil.ErrorResponse errorResponse = HttpClientUtil.handleErrorResponse(response);
                    if (errorResponse != null) {
                        cellFunctionsController.showInfoAlert(errorResponse.getError());
                    } else {
                        cellFunctionsController.showInfoAlert("Error loading sheets.");
                    }
                } else {
                    Type mapType = new TypeToken<Map<Integer, SheetDtoResp>>() {
                    }.getType();
                    Map<Integer, SheetDtoResp> sheets = GSON.fromJson(response.body().string(), mapType);
                    Platform.runLater(() -> {
                        try {
                            cellFunctionsController.showVersion(sheets.get(selectedVersion), "Version Number: " + selectedVersion.toString());
                        } catch (Exception e) {
                            cellFunctionsController.showInfoAlert(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void noNameRangeSelected(String fromCell, String toCell, TableFunctionalityController.ConfirmType type) {
        String params = fromCell.trim() + ".." + toCell.trim();
        query.clear();
        query.put(SHEET_ID, currSheet);
        HttpClientUtil.RangeBody rangeBody = new HttpClientUtil.RangeBody("", params);
        httpCallerService.getNoNameRange(query, rangeBody, null, NO_NAME_RANGE, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    List<CellDataDtoResp> cellList = GSON.fromJson(response.body().string(), new TypeToken<List<CellDataDtoResp>>() {
                    }.getType());
                    Set<CellDataDtoResp> cells = new HashSet<>(cellList);
                    Platform.runLater(() -> {
                        if (type.equals(TableFunctionalityController.ConfirmType.FILTER_RANGE))
                            tableFunctionalityController.filterColumnPopup(cells, fromCell, toCell);
                        else tableFunctionalityController.sortColumnPopup(fromCell.toUpperCase(), toCell.toUpperCase());
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
            }
        });
    }

    public void filterButtonClicked() {
        tableFunctionalityController.buildNoNameRangePopup(TableFunctionalityController.ConfirmType.FILTER_RANGE);
    }

    public void sortButtonClicked() {
        tableFunctionalityController.buildNoNameRangePopup(TableFunctionalityController.ConfirmType.SORT_RANGE);
    }

    public void filterParamsConfirmed(String fromCell, String
            toCell, Map<String, Set<String>> filterBy, String selectedFilterType) {
        query.clear();
        query.put(SHEET_ID, currSheet);
        HttpClientUtil.FilterObj filterObj = new HttpClientUtil.FilterObj(fromCell.trim() + ".." + toCell.trim(), filterBy, selectedFilterType);
        httpCallerService.filterSheet(query, filterObj, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    SheetDtoResp filteredSheet = GSON.fromJson(response.body().string(), SheetDtoResp.class);
                    Platform.runLater(() -> {
                        try {
                            showSheetPopup(filteredSheet,
                                    "Filtered from " + fromCell + " to " + toCell + " | By : " + filterBy);
                        } catch (IOException e) {
                            tableFunctionalityController.showInfoAlert(e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
            }
        });
    }

    public void sortParamsConfirmed(String fromCell, String toCell, List<String> sortBy) {
        HttpClientUtil.SortObj sortObj = new HttpClientUtil.SortObj(fromCell.trim() + ".." + toCell.trim(), sortBy);
        query.clear();
        query.put(SHEET_ID, currSheet);
        httpCallerService.sortSheet(query, sortObj, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    SheetDtoResp sortedSheet = GSON.fromJson(response.body().string(), SheetDtoResp.class);
                    Platform.runLater(() -> {
                        try {
                            showSheetPopup(sortedSheet,
                                    "Sorted from " + fromCell + " to " + toCell + " | By : " + sortBy);
                        } catch (IOException e) {
                            tableFunctionalityController.showInfoAlert(e.getMessage());
                        }
                    });
                } catch (RuntimeException e) {
                    Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
            }
        });

    }

    public void showSheetPopup(SheetDtoResp sheet, String title) throws IOException {
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
        theme = value;
        if (value.equals("Theme 1"))
            setNewTheme("Theme1");
        else if (value.equals("Theme 2"))
            setNewTheme("Theme2");
        else
            setNewTheme("Default");
    }

    private void setNewTheme(String value) {
        tableFunctionalityController.setTheme(value);
        cellFunctionsController.setTheme(value);
        gridSheetController.setTheme(value);
        titleCardController.setTheme(value);
    }

    public void dynamicChangeButtonClicked() {
        query.clear();
        query.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, cellFunctionsController.getCellIdFocused()));
        httpCallerService.saveCellValueForDynamicChange(query, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    Platform.runLater(() -> {
                        disableComponents(true);
                        cellFunctions.setDisable(false);
                        gridSheet.opacityProperty().setValue(1);
                        cellFunctionsController.setDynamicFuncDisable(true);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> cellFunctionsController.showInfoAlert(e.getMessage()));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> cellFunctionsController.showInfoAlert(e.getMessage()));
            }
        });

    }

    public void updateCellDynamically(String cellId, String newOriginalValue) {
        query.clear();
        query.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, cellId));

        httpCallerService.startDynamicChange(query, newOriginalValue, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    SheetDtoResp dynamicDto = GSON.fromJson(response.body().string(), SheetDtoResp.class);
                    Platform.runLater(() -> gridSheetController.populateTableView(dynamicDto, false));
                } catch (Exception e) {
                    Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> cellFunctionsController.showInfoAlert(e.getMessage()));
            }
        });

    }

    public void dynamicCancelClicked() {
        cellFunctionsController.exitDynamicChange();
    }

    public void exitDynamicChangeClicked() {
        disableComponents(false);
        cellFunctionsController.setDynamicFuncDisable(false);
        query.clear();
        query.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, cellFunctionsController.getCellIdFocused()));
        httpCallerService.stopDynamicChange(query, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    httpCallerService.handleErrorResponse(response);
                    currSheetDtoResp = GSON.fromJson(response.body().string(), SheetDtoResp.class);
                    Platform.runLater(() -> {
                        gridSheetController.populateTableView(currSheetDtoResp, false);
                        cellFunctionsController.showCell(
                                currSheetDtoResp.getActiveCells().get(cellFunctionsController.getCellIdFocused()));
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> cellFunctionsController.showInfoAlert(e.getMessage()));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> cellFunctionsController.showInfoAlert(e.getMessage()));
            }
        });

    }

    public void chartButtonClicked() {
        chartMaker.createChartDialogPopup(currSheetDtoResp.getColSize());
    }

    public void confirmChartClicked(String chartType, String paramsX, String paramsY) {
        query.clear();
        query.put(SHEET_ID, currSheet);
        HttpClientUtil.Ranges ranges = new HttpClientUtil.Ranges(paramsX, paramsY);
        httpCallerService.getNoNameRange(query, null, ranges, NO_NAME_RANGES, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    httpCallerService.handleErrorResponse(response);
                    Type rangesType = new TypeToken<HttpClientUtil.Ranges>() {
                    }.getType();
                    HttpClientUtil.Ranges newRanges = HttpClientUtil.GSON.fromJson(response.body().string(), rangesType);
                    Platform.runLater(() -> {
                        List<Double> xValues = getNumericValuesSortedByRow(newRanges.getXRange());
                        List<Double> yValues = getNumericValuesSortedByRow(newRanges.getYRange());
                        if (chartType.equals("Bar Chart"))
                            chartMaker.createBarChart(xValues, yValues);
                        else {
                            chartMaker.createLineChart(xValues, yValues);
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> tableFunctionalityController.showInfoAlert(e.getMessage()));
                }
            }

        });
    }

    //because I used set in so much code...
    private List<Double> getNumericValuesSortedByRow(Set<CellDataDtoResp> cells) {
        return cells.stream()
                .filter(cell -> cell.getEffectiveValue().getObjType() == ObjType.NUMERIC)
                .sorted(Comparator.comparingInt(CellDataDtoResp::getRow))
                .map(cell -> (double) cell.getEffectiveValue().getValue())
                .collect(Collectors.toList());
    }

    public void backToMainScreenClicked() {
        try {
            stopRefresher();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/page/view/mainscreen/mainScreen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 915, 710);
            stage.setScene(scene);
            stage.setTitle("Sheet Cell - Main Screen");
            MainScreenController controller = loader.getController();
            controller.setUserName(this.userName); // need to give it the username.
            controller.setStage(stage);
            controller.setTheme(theme);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserName(String name) {
        this.userName = name;
        titleCardController.setName(name);
    }

    public void stopRefresher() {
        titleCardController.stopVersionRefresher();
    }
}