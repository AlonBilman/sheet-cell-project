package components.page.view.sheetscreen;

import com.google.gson.reflect.TypeToken;
import components.body.table.func.TableFunctionalityController;
import components.body.table.view.GridSheetController;
import components.header.cellfunction.CellFunctionsController;
import components.header.loadfile.LoadFileController;
import components.header.title.TitleCardController;
import components.page.view.mainscreen.MainScreenController;
import constants.Constants;
import dto.CellDataDTO;
import dto.sheetDTO;
import http.CallerService;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static constants.Constants.*;


public class AppController {

    private ChartMaker chartMaker;
    private SheetManagerImpl engine = new SheetManagerImpl();
    private CallerService httpCallerService;
    private Map<String, String> quary;
    private String currSheet;
    private Stage stage;


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

    public void setStage(Stage stage) {
        this.stage = stage;
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
            httpCallerService = new CallerService();
            quary = new HashMap<>();

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

    private void loadFileLogic(File file) {
        disableComponents(false);
        try {
            httpCallerService.uploadFileAsync(file, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                        loadFileController.showInfoAlert("File upload failed." + e.getMessage());
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        httpCallerService.handleErrorResponse(response);
                        String sheetName = HttpClientUtil.handleStringResponse(response);
                        quary.clear();
                        quary.put(Constants.SHEET_ID, sheetName);
                        httpCallerService.fetchSheetAsync(quary, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Platform.runLater(() -> {
                                    loadFileController.showInfoAlert("Failed to fetch sheet data." + e.getMessage());
                                });
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                try {
                                    httpCallerService.handleErrorResponse(response);
                                    sheetDTO sheet = GSON.fromJson(response.body().string(), sheetDTO.class);
                                    Platform.runLater(() -> {
                                        gridSheetController.populateTableView(sheet, true);
                                        loadFileController.editFilePath(file.getAbsolutePath());
                                        tableFunctionalityController.setActiveButtons(
                                                TableFunctionalityController.ButtonState.LOADING_FILE, true);
                                        cellFunctionsController.wakeVersionButton();
                                        currSheet = sheetName;
                                    });
                                } catch (Exception e) {
                                    Platform.runLater(() -> {
                                        loadFileController.showInfoAlert(e.getMessage());
                                    });
                                }
                                response.close();
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            loadFileController.showInfoAlert(e.getMessage());
                        });
                    }
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                loadFileController.showInfoAlert(e.getMessage());
            });
        }
    }

    public void checkAndLoadFile(File file) {
        disableComponents(true);
        loadFileController.taskLoadingSimulation(() -> {
            loadFileLogic(file);
        });
    }

    public void CellClicked(String id) {
        outOfFocus();
        quary.clear();
        quary.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, id));
        httpCallerService.fetchCellAsync(quary, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    cellFunctionsController.showInfoAlert("Failed to fetch cell data.");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Platform.runLater(() -> {
                        cellFunctionsController.showInfoAlert("Error fetching cell data.");
                    });
                    return;
                }

                CellDataDTO cell = GSON.fromJson(response.body().string(), CellDataDTO.class);
                quary.remove(CELL_ID);

                httpCallerService.fetchSheetAsync(quary, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() -> {
                            cellFunctionsController.showInfoAlert("Failed to fetch sheet data.");
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            sheetDTO sheet = GSON.fromJson(response.body().string(), sheetDTO.class);
                            Platform.runLater(() -> {
                                cellFunctionsController.showCell(cell);
                                tableFunctionalityController.setActiveButtons(
                                        TableFunctionalityController.ButtonState.CLICKING_CELL, true);
                                gridSheetController.colorizeImportantCells(sheet, id);
                                if (cell.getEffectiveValue().getObjType().equals(ObjType.NUMERIC)) {
                                    cellFunctionsController.showNumericButtons(true);
                                }
                            });
                        } else {
                            Platform.runLater(() -> {
                                cellFunctionsController.showInfoAlert("Error loading sheet data.");
                            });
                        }
                        response.close();
                    }
                });
            }
        });
    }

    public void loadClicked() {
        cellFunctionsController.outOfFocus();
        tableFunctionalityController.outOfFocus();
    }

    public void updateCellClicked(String cellToUpdate, String newOriginalValue) {
        quary.clear();
        quary.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, cellToUpdate));
        httpCallerService.changeCellAsync(quary, newOriginalValue, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    cellFunctionsController.showInfoAlert(e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    HttpClientUtil.ErrorResponse errorResponse = HttpClientUtil.handleErrorResponse(response);
                    Platform.runLater(() -> {
                        if (errorResponse != null)
                            cellFunctionsController.showInfoAlert(errorResponse.getError());
                        else {
                            cellFunctionsController.showInfoAlert("Error loading data.");
                        }
                    });
                }
                quary.remove(CELL_ID);
                httpCallerService.fetchSheetAsync(quary, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Platform.runLater(() -> {
                            cellFunctionsController.showInfoAlert(e.getMessage());
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            HttpClientUtil.ErrorResponse errorResponse = HttpClientUtil.handleErrorResponse(response);
                            Platform.runLater(() -> {
                                if (errorResponse != null)
                                    cellFunctionsController.showInfoAlert(errorResponse.getError());
                                else {
                                    cellFunctionsController.showInfoAlert("Error loading sheet.");
                                }
                            });
                        }
                        sheetDTO sheet = GSON.fromJson(response.body().string(), sheetDTO.class);
                        Platform.runLater(() -> {
                            outOfFocus();
                            gridSheetController.populateTableView(sheet, false);
                        });
                    }
                });
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
        quary.clear();
        quary.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, id));
        String color;
        if (selectedColor != null)
            color = selectedColor.toString();
        else {
            color = null;
        }
        httpCallerService.changeColorAsync(quary, CELL_BACKGROUND_COLOR, color, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    cellFunctionsController.showInfoAlert(e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    HttpClientUtil.ErrorResponse errorResponse = HttpClientUtil.handleErrorResponse(response);
                    if (errorResponse != null)
                        cellFunctionsController.showInfoAlert(errorResponse.getError());
                    else
                        cellFunctionsController.showInfoAlert("Error updating color.");
                }
                Platform.runLater(() -> {
                    if (color != null)
                        gridSheetController.changeBackgroundColor(id, selectedColor);
                });
            }
        });

    }

    public void textColorPicked(Color selectedColor) {
        String id = cellFunctionsController.getCellIdFocused();
        quary.clear();
        quary.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, id));
        String color;
        if (selectedColor != null)
            color = selectedColor.toString();
        else {
            color = null;
        }
        try {
            httpCallerService.changeColorAsync(quary, CELL_TEXT_COLOR, color, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                        cellFunctionsController.showInfoAlert(e.getMessage());
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        HttpClientUtil.ErrorResponse errorResponse = HttpClientUtil.handleErrorResponse(response);
                        if (errorResponse != null)
                            cellFunctionsController.showInfoAlert(errorResponse.getError());
                        else
                            cellFunctionsController.showInfoAlert("Error updating color.");
                    }
                    Platform.runLater(() -> {
                        if (color != null)
                            gridSheetController.changeTextColor(id, selectedColor);
                    });
                }
            });
        } catch (Exception e) {
            cellFunctionsController.showInfoAlert(e.getMessage());
        }
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
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        httpCallerService.addRange(quary, rangeBody, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    tableFunctionalityController.showInfoAlert(e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        tableFunctionalityController.showInfoAlert(e.getMessage());
                    });
                }
            }
        });
    }

    public void deleteRangeConfirmedClicked(String rangeToDelete) {
        outOfFocus();
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        HttpClientUtil.RangeBody range = new HttpClientUtil.RangeBody(rangeToDelete, "");

        httpCallerService.deleteRange(quary, range, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        tableFunctionalityController.showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    tableFunctionalityController.showInfoAlert(e.getMessage());
                });
            }
        });
    }

    public void showRangeConfirmedClicked(String selectedRangeName) {
        outOfFocus();
        Set<String> labelNamesToFocus = new HashSet<>();
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        httpCallerService.fetchSheetAsync(quary, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    sheetDTO sheet = GSON.fromJson(response.body().string(), sheetDTO.class);
                    Platform.runLater(() -> {
                        Set<CellDataDTO> focusCells = sheet.getActiveRanges()
                                .get(selectedRangeName)
                                .getCells();
                        for (CellDataDTO focusCell : focusCells) {
                            labelNamesToFocus.add(focusCell.getId());
                        }
                        gridSheetController.focusOnRangeCells(labelNamesToFocus);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        tableFunctionalityController.showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                tableFunctionalityController.showInfoAlert(e.getMessage());
            }
        });
    }

    public void getVersionClicked() {
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        httpCallerService.fetchSheetAsync(quary, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    cellFunctionsController.showInfoAlert(e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    HttpClientUtil.ErrorResponse errorResponse = HttpClientUtil.handleErrorResponse(response);
                    Platform.runLater(() -> {
                        if (errorResponse != null)
                            cellFunctionsController.showInfoAlert(errorResponse.getError());
                        else {
                            cellFunctionsController.showInfoAlert("Error loading sheet.");
                        }
                    });
                }
                sheetDTO sheet = GSON.fromJson(response.body().string(), sheetDTO.class);
                Platform.runLater(() -> {
                    cellFunctionsController.buildVersionPopup(sheet.getSheetVersionNumber());
                });
            }
        });

    }

    public void confirmVersionClicked(Integer selectedVersion) {
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        httpCallerService.fetchSheetsAsync(quary, new Callback() {
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
                    Type mapType = new TypeToken<Map<Integer, sheetDTO>>() {
                    }.getType();
                    Map<Integer, sheetDTO> sheets = GSON.fromJson(response.body().string(), mapType);
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
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        HttpClientUtil.RangeBody rangeBody = new HttpClientUtil.RangeBody("", params);
        httpCallerService.getNoNameRange(quary, rangeBody, null, NO_NAME_RANGE, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    List<CellDataDTO> cellList = GSON.fromJson(response.body().string(), new TypeToken<List<CellDataDTO>>() {
                    }.getType());
                    Set<CellDataDTO> cells = new HashSet<>(cellList);
                    Platform.runLater(() -> {
                        if (type.equals(TableFunctionalityController.ConfirmType.FILTER_RANGE))
                            tableFunctionalityController.filterColumnPopup(cells, fromCell, toCell);
                        else tableFunctionalityController.sortColumnPopup(fromCell.toUpperCase(), toCell.toUpperCase());
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        tableFunctionalityController.showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    tableFunctionalityController.showInfoAlert(e.getMessage());
                });
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
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        HttpClientUtil.FilterObj filterObj = new HttpClientUtil.FilterObj(fromCell.trim() + ".." + toCell.trim(), filterBy, selectedFilterType);
        httpCallerService.filterSheet(quary, filterObj, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    sheetDTO filteredSheet = GSON.fromJson(response.body().string(), sheetDTO.class);
                    Platform.runLater(() -> {
                        try {
                            showSheetPopup(filteredSheet,
                                    "Filtered from " + fromCell + " to " + toCell + " | By : " + filterBy);
                        } catch (IOException e) {
                            tableFunctionalityController.showInfoAlert(e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        tableFunctionalityController.showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    tableFunctionalityController.showInfoAlert(e.getMessage());
                });
            }
        });
    }

    public void sortParamsConfirmed(String fromCell, String toCell, List<String> sortBy) {
        HttpClientUtil.SortObj sortObj = new HttpClientUtil.SortObj(fromCell.trim() + ".." + toCell.trim(), sortBy);
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        httpCallerService.sortSheet(quary, sortObj, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    sheetDTO sortedSheet = GSON.fromJson(response.body().string(), sheetDTO.class);
                    Platform.runLater(() -> {
                        try {
                            showSheetPopup(sortedSheet,
                                    "Sorted from " + fromCell + " to " + toCell + " | By : " + sortBy);
                        } catch (IOException e) {
                            tableFunctionalityController.showInfoAlert(e.getMessage());
                        }
                    });
                } catch (RuntimeException e) {
                    Platform.runLater(() -> {
                        tableFunctionalityController.showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    tableFunctionalityController.showInfoAlert(e.getMessage());
                });
            }
        });

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
        quary.clear();
        quary.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, cellFunctionsController.getCellIdFocused()));
        httpCallerService.saveCellValueForDynamicChange(quary, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    Platform.runLater(() -> {
                        disableComponents(true);
                        cellFunctions.setDisable(false);
                        gridSheet.opacityProperty().setValue(1);
                        cellFunctionsController.setDynamicFuncDisable(true);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        cellFunctionsController.showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    cellFunctionsController.showInfoAlert(e.getMessage());
                });
            }
        });

    }

    public void updateCellDynamically(String cellId, String newOriginalValue) {
        quary.clear();
        quary.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, cellId));

        httpCallerService.startDynamicChange(quary, newOriginalValue, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    sheetDTO dynamicDto = GSON.fromJson(response.body().string(), sheetDTO.class);
                    Platform.runLater(() -> {
                        gridSheetController.populateTableView(dynamicDto, false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        tableFunctionalityController.showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    cellFunctionsController.showInfoAlert(e.getMessage());
                });
            }
        });

    }

    public void dynamicCancelClicked() {
        cellFunctionsController.exitDynamicChange();
    }

    public void exitDynamicChangeClicked() {
        disableComponents(false);
        cellFunctionsController.setDynamicFuncDisable(false);
        quary.clear();
        quary.putAll(Map.of(SHEET_ID, currSheet, CELL_ID, cellFunctionsController.getCellIdFocused()));
        httpCallerService.stopDynamicChange(quary, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    sheetDTO oldDto = GSON.fromJson(response.body().string(), sheetDTO.class);
                    Platform.runLater(() -> {
                        gridSheetController.populateTableView(oldDto, false);
                        cellFunctionsController.showCell(
                                oldDto.getActiveCells().get(cellFunctionsController.getCellIdFocused()));
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        cellFunctionsController.showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    cellFunctionsController.showInfoAlert(e.getMessage());
                });
            }
        });

    }

    public void chartButtonClicked() {
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        httpCallerService.fetchSheetAsync(quary, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    tableFunctionalityController.showInfoAlert(e.getMessage());
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    sheetDTO sheet = GSON.fromJson(response.body().string(), sheetDTO.class);
                    Platform.runLater(() -> {
                        chartMaker.createChartDialogPopup(sheet.getColSize());
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        tableFunctionalityController.showInfoAlert(e.getMessage());
                    });
                }
            }
        });
    }

    public void confirmChartClicked(String chartType, String paramsX, String paramsY) {
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        HttpClientUtil.Ranges ranges = new HttpClientUtil.Ranges(paramsX, paramsY);
        httpCallerService.getNoNameRange(quary, null, ranges, NO_NAME_RANGES, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    tableFunctionalityController.showInfoAlert(e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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
                    Platform.runLater(() -> {
                        tableFunctionalityController.showInfoAlert(e.getMessage());
                    });
                }
            }

        });
    }

    //because I used set in so much code...
    private List<Double> getNumericValuesSortedByRow(Set<CellDataDTO> cells) {
        return cells.stream()
                .filter(cell -> cell.getEffectiveValue().getObjType() == ObjType.NUMERIC)
                .sorted(Comparator.comparingInt(CellDataDTO::getRow))
                .map(cell -> (double) cell.getEffectiveValue().getValue())
                .collect(Collectors.toList());
    }

    public void deleteOrViewExistingRangeClicked(TableFunctionalityController.ConfirmType type) throws IOException {
        quary.clear();
        quary.put(SHEET_ID, currSheet);
        httpCallerService.fetchSheetAsync(quary, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                tableFunctionalityController.showInfoAlert(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    sheetDTO sheet = GSON.fromJson(response.body().string(), sheetDTO.class);
                    Set<String> rangeNames = sheet.getActiveRanges().keySet(); // get the names
                    rangeNames = rangeNames.stream()
                            .sorted() // sort the names
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    Set<String> finalRangeNames = rangeNames;
                    Platform.runLater(() -> {
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
                    });
                } else {
                    HttpClientUtil.ErrorResponse errorResponse = HttpClientUtil.handleErrorResponse(response);
                    Platform.runLater(() -> {
                        if (errorResponse != null) {
                            tableFunctionalityController.showInfoAlert(errorResponse.getError());
                        } else {
                            tableFunctionalityController.showInfoAlert("Error loading sheet.");
                        }
                    });
                }
            }
        });
    }

    public void backToMainScreenClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../mainscreen/mainScreen.fxml")); // Update the path accordingly
            Parent root = loader.load();

            Scene scene = new Scene(root, 1120, 800);
            stage.setScene(scene);
            stage.setTitle("Sheet Cell - Main Screen");

            // Pass the stage back to the main screen controller if needed
            MainScreenController controller = loader.getController();
            controller.setStage(stage);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}