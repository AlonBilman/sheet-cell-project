package components.body.table.view;

import components.main.AppController;
import dto.CellDataDTO;
import dto.sheetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;

import java.util.*;

public class GridSheetController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    private AppController appController;

    private Map<String, Label> labelMap;
    private Map<String, Background> originalBackgrounds = new HashMap<>();
    private List<Label> focusedOn;
    private Map<String, Label> borderMap;

    public void initialize() {
        gridPane.getStyleClass().add("grid-pane");
        scrollPane.getStyleClass().add("scroll-pane");
        labelMap = new HashMap<>();
        focusedOn = new ArrayList<>();
        borderMap = new HashMap<>();
    }

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void clearGridPane() {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        labelMap.clear();
        borderMap.clear();
    }

    private void addBorders(int rows, int cols, int maxRow, int maxCol) {
        Label emptyLabel = new Label();
        gridPane.add(emptyLabel, 0, 0);
        for (int i = 1; i <= cols; i++) {
            Label cellLabel = new Label();
            cellLabel.setText(String.valueOf((char) ('A' + (i - 1))));
            cellLabel.setMinSize(maxCol, 10);
            borderMap.put(cellLabel.getText(), cellLabel);
            gridPane.add(cellLabel, i, 0);
            setBoardersFunctionality(cellLabel);
        }
        for (int i = 1; i <= rows; i++) {
            Label cellLabel = new Label();
            cellLabel.setText(String.valueOf(i));
            cellLabel.setMinSize(20, maxRow);
            borderMap.put(cellLabel.getText(), cellLabel);
            gridPane.add(cellLabel, 0, i);
            setBoardersFunctionality(cellLabel);
        }
    }

    public void populateTableView(sheetDTO sheetCopy, boolean isLoad) {
        int row = sheetCopy.getRowSize();
        int col = sheetCopy.getColSize();
        int maxRow = sheetCopy.getRowHeight();
        int maxCol = sheetCopy.getColWidth();
        Map<String, CellDataDTO> cells = sheetCopy.getActiveCells();

        if (isLoad) {
            labelMap.clear();
            originalBackgrounds.clear();
            clearGridPane();
            addBorders(row, col, maxRow, maxCol);
        }

        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= col; j++) {
                String id = String.valueOf((char) ('A' + (j - 1))) + i;
                Label cellLabel;

                if (isLoad) {
                    cellLabel = new Label();
                    setCellFunctionality(cellLabel, maxRow, maxCol, id);
                    gridPane.add(cellLabel, j, i);
                    labelMap.put(id, cellLabel);
                } else {
                    cellLabel = getNodeFromGridPane(id);
                }

                updateCellLabel(cellLabel, cells.get(id));
            }
        }
    }

    private void updateCellLabel(Label cellLabel, CellDataDTO cellData) {
        if (cellData == null) {
            cellLabel.setText("");
        } else {
            Object value = cellData.getEffectiveValue().getValue();
            if (value instanceof Boolean) {
                cellLabel.setText(value.toString().toUpperCase());
            } else {
                cellLabel.setText(value.toString());
            }
        }
    }

    private Label getNodeFromGridPane(String labelId) {
        return labelMap.get(labelId);
    }

    public void changeGridPaneStyle(AppController.Style style) {
        gridPane.getStyleClass().clear();
        changeGridStyle(style);
    }

    private void changeGridStyle(AppController.Style style) {
        switch (style) {
            case DEFAULT_STYLE -> {
                gridPane.getStyleClass().add("grid-pane");
                break;
            }
            case DARK_MODE -> {
                gridPane.getStyleClass().add("grid-pane-dark-mode");
                break;
            }
        }
    }

    private void changeLabelStyle(Label label, AppController.Style style) {
        switch (style) {
            case DEFAULT_STYLE -> {
                label.getStyleClass().add("cell-default");
                break;
            }
            case DARK_MODE -> {
                label.getStyleClass().add("cell-dark-mode");
                break;
            }
        }
    }

    private void setCellFunctionality(Label cellLabel, int maxRowHeight, int maxColWidth, String cellId) {
        cellLabel.setPrefSize(maxColWidth, maxRowHeight);
        changeLabelStyle(cellLabel, appController.getStyleChosen());
        setColorsIfNeeded(cellId);
        cellLabel.setOnMouseClicked(event -> appController.CellClicked(cellId));
    }

    private void setColorsIfNeeded(String cellId) {
        appController.getColorsFromEngine(cellId);
    }

    private void setBoardersFunctionality(Label cellLabel) {
        cellLabel.getStyleClass().add("boarder");
        cellLabel.setOnMouseClicked(event -> appController.BoarderClicked(cellLabel.getText()));
    }


    public void updateAllCellStyles(AppController.Style style) {
        // Loop through all cells in the labelMap
        for (Label label : labelMap.values()) {
            // Reset and update each label's style
            label.getStyleClass().clear(); // Clear previous styles
            changeLabelStyle(label, style); // Apply the new style
        }
        for (Label label : borderMap.values()) {
            label.getStyleClass().clear();
            changeLabelStyle(label, style);
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void colorizeImportantCells(sheetDTO curr, String id) {
        Label currCell = labelMap.get(id);
        currCell.getStyleClass().add("cell-selected");
        focusedOn.add(currCell);
        Map<String, CellDataDTO> cells = curr.getActiveCells();
        CellDataDTO cellData = cells.get(id);
        Set<String> affectsOn = cellData.getAffectsOn();
        Set<String> depOn = cellData.getDependsOn();

        for (String dep : depOn) {
            Label label = labelMap.get(dep);
            focusedOn.add(label);
            label.getStyleClass().add("cell-dependsOn");
        }

        for (String affect : affectsOn) {
            Label label = labelMap.get(affect);
            focusedOn.add(label);
            label.getStyleClass().add("cell-affectsOn");
        }
    }

    public void returnOldColors() {
        for (Label label : focusedOn)
            label.getStyleClass().removeAll("cell-dependsOn", "cell-affectsOn", "cell-selected", "focused-on");
        focusedOn.clear();
    }

    public void changeTextColor(String cellId, Color newColor) {
        Label label = labelMap.get(cellId);
        changeCellCssId(label, "cell-default", "cell-non-default");
        label.setTextFill(newColor);
    }

    public void changeBackgroundColor(String cellId, Color newColor) {
        originalBackgrounds.remove(cellId);
        originalBackgrounds.put(cellId, new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, null)));
        Label label = labelMap.get(cellId);
        changeCellCssId(label, "cell-default", "cell-non-default");
        label.setBackground(originalBackgrounds.get(cellId));
    }

    private void removeLabelLayout(Label label) {
        label.setBackground(null);
        label.setTextFill(Paint.valueOf("Black"));
        label.setAlignment(null);
    }

    private void changeCellCssId(Label label, String remove, String add) {
        label.getStyleClass().remove(remove);
        label.getStyleClass().add(add);
    }

    public void resetToDefaultColors(Label label, String id) {
        originalBackgrounds.remove(id);
        changeCellCssId(label, "cell-non-default", "cell-default");
    }

    public void updateSize(double size, String id) {
        if (Character.isDigit(id.charAt(0))) {
            borderMap.get(id).setMinHeight(size);
            for (Label label : focusedOn)
                label.setMinHeight(size);
            return;
        }
        borderMap.get(id).setMinWidth(size);
        for (Label label : focusedOn)
            label.setMinWidth(size);
    }

    public void focusOnRangeCells(Set<String> cellsId) {
        for (String cellId : cellsId) {
            Label label = labelMap.get(cellId);
            focusedOn.add(label);
            label.getStyleClass().add("focused-on");
        }
    }

    public void focusOnBorderAbilityCells(String borderId) {
        if (Character.isDigit(borderId.charAt(0))) {
            for (int i = 1; i < gridPane.getColumnCount(); i++) {
                char columnLetter = (char) ('A' + (i - 1));
                Label label = labelMap.get(columnLetter + borderId);
                focusedOn.add(label);
                label.getStyleClass().add("focused-on");
            }
        } else {
            for (int i = 1; i < gridPane.getRowCount(); i++) {
                Label label = labelMap.get(borderId + i);
                focusedOn.add(label);
                label.getStyleClass().add("focused-on");
            }
        }
    }

    public void updateAliment(String alignment, String id) {
        for (Label label : focusedOn) {
            changeCellCssId(label, "cell-default", "cell-non-default");
            resetToDefaultAlignment(label, id);
            if (alignment.equals("Left")) {
                label.getStyleClass().add("alignment-left");
            } else if (alignment.equals("Right")) {
                label.getStyleClass().add("alignment-right");
            }
        }
    }

    public void resetToDefault(String id) {
        Label label = labelMap.get(id);
        removeLabelLayout(label);
        resetToDefaultColors(label, id);
        resetToDefaultAlignment(label, id);
    }

    private void resetToDefaultAlignment(Label label, String id) {
        label.getStyleClass().removeAll("alignment-left", "alignment-right");
    }

    public void disableGridPane() {
        gridPane.setDisable(true);
    }

    public void enableGridPane() {
        gridPane.setDisable(false);
    }

}
