package components.body.table.view;

import components.main.AppController;
import dto.CellDataDTO;
import dto.sheetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GridSheetController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    private AppController appController;

    Map<String, Label> labelMap;

    private Set<String> highlightedDependencyCells = new HashSet<>();
    private Set<String> highlightedAffectCells = new HashSet<>();
    private Map<String, Background> originalBackgrounds = new HashMap<>();

    public void initialize() {
        // Set fixed size for the GridPane
        gridPane.setPrefSize(600, 400);
        gridPane.setMinSize(600, 400);
        gridPane.setMaxSize(600, 400);
        // Ensure the ScrollPane reacts to the overflow
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        labelMap = new HashMap<>();
    }

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void clearGridPane() {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        labelMap.clear();
    }

    private void addBorders(int rows, int cols) {
        Label emptyLabel = new Label();
        gridPane.add(emptyLabel, 0, 0);

        for (int i = 1; i <= cols; i++) {
            Label cellLabel = new Label();
            cellLabel.setText(String.valueOf((char) ('A' + (i - 1))));
            cellLabel.getStyleClass().add("column-header"); // Apply column header class
            cellLabel.setMinSize(10, 10);
            gridPane.add(cellLabel, i, 0);
            makeClickVisuallyClicked(cellLabel);
        }
        for (int i = 1; i <= rows; i++) {
            Label cellLabel = new Label();
            cellLabel.setText(String.valueOf(i));
            cellLabel.getStyleClass().add("row-header"); // Apply row header class
            cellLabel.setMinSize(10, 10);
            gridPane.add(cellLabel, 0, i);
            makeClickVisuallyClicked(cellLabel);
        }
        // no need any functionality for them for now.
    }

    public void populateTableView(sheetDTO sheetCopy, boolean isInitialLoad) {
        int row = sheetCopy.getRowSize();
        int col = sheetCopy.getColSize();
        int maxRow = sheetCopy.getRowHeight();
        int maxCol = sheetCopy.getColWidth();
        Map<String, CellDataDTO> cells = sheetCopy.getActiveCells();

        if (isInitialLoad) {
            clearGridPane();
            addBorders(row, col);
        }

        // Loop to add or update cells in the grid
        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= col; j++) {
                String id = String.valueOf((char) ('A' + (j - 1))) + i;
                Label cellLabel;

                if (isInitialLoad) {
                    cellLabel = new Label();
                    setCellFunctionality(cellLabel, maxCol, maxRow, id);
                    gridPane.add(cellLabel, j, i);
                    labelMap.put(id, cellLabel);
                } else {
                    cellLabel = getNodeFromGridPane(id); // Get existing label
                }

                // Update cell data
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

    private void makeClickVisuallyClicked(Label cellLabel) {
        cellLabel.setOnMousePressed(event -> cellLabel.getStyleClass().add("pressed"));
        cellLabel.setOnMouseReleased(event -> cellLabel.getStyleClass().remove("pressed"));
    }

    private void setCellFunctionality(Label cellLabel, int maxRowHeight, int maxColWidth, String cellId) {
        cellLabel.setPrefSize(maxColWidth, maxRowHeight);
        cellLabel.setAlignment(Pos.CENTER);
        cellLabel.getStyleClass().add("label"); // Apply base label class
        final Background[] originalBackground = new Background[1];

        cellLabel.setOnMouseEntered(event -> {
            originalBackground[0] = cellLabel.getBackground();  // Save the original background
            Color currentColor = ((Color) cellLabel.getBackground().getFills().get(0).getFill());
            Color hoverColor = currentColor.interpolate(Color.LIGHTGRAY, 0.5);
            cellLabel.setBackground(new Background(new BackgroundFill(hoverColor, CornerRadii.EMPTY, null)));
        });

        cellLabel.setOnMouseExited(event -> cellLabel.setBackground(originalBackground[0]));  // Restore the original background

        makeClickVisuallyClicked(cellLabel);
        cellLabel.setOnMouseClicked(event -> appController.CellClicked(cellId));
    }


    public void returnOldColors() {
        for (String id : originalBackgrounds.keySet()) {
            Label label = labelMap.get(id);
            label.setBackground(originalBackgrounds.get(id));
        }
    }

    public void colorizeImportantCells(sheetDTO curr, String id) {
        // Clear previously highlighted cells first
        clearHighlightedCells();

        Map<String, CellDataDTO> cells = curr.getActiveCells();
        CellDataDTO cellData = cells.get(id);
        Set<String> affectsOn = cellData.getAffectsOn();
        Set<String> depOn = cellData.getDependsOn();

        // Highlight new dependency cells
        for (String dep : depOn) {
            Label label = labelMap.get(dep);
            if (!originalBackgrounds.containsKey(dep)) {
                originalBackgrounds.put(dep, label.getBackground());
            }
            label.getStyleClass().add("dependency-cell");
            highlightedDependencyCells.add(dep);  // Track the highlighted cells
        }

        // Highlight new affect cells
        for (String affect : affectsOn) {
            Label label = labelMap.get(affect);
            if (!originalBackgrounds.containsKey(affect)) {
                originalBackgrounds.put(affect, label.getBackground());
            }
            label.getStyleClass().add("affect-cell");
            highlightedAffectCells.add(affect);  // Track the highlighted cells
        }
    }

    private void clearHighlightedCells() {
        // Remove the 'dependency-cell' class from previously highlighted cells
        for (String dep : highlightedDependencyCells) {
            Label label = labelMap.get(dep);
            label.getStyleClass().remove("dependency-cell");
        }
        highlightedDependencyCells.clear();  // Clear the list after removing the styles

        // Remove the 'affect-cell' class from previously highlighted cells
        for (String affect : highlightedAffectCells) {
            Label label = labelMap.get(affect);
            label.getStyleClass().remove("affect-cell");
        }
        highlightedAffectCells.clear();  // Clear the list after removing the styles
    }

    public void changeTextColor(String cellId, Color newColor) {
        Label label = labelMap.get(cellId);
        label.setTextFill(newColor);
    }

    public void changeBackgroundColor(String cellId, Color newColor) {
        originalBackgrounds.remove(cellId);
        originalBackgrounds.put(cellId, new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, null)));
        Label label = labelMap.get(cellId);
        label.setBackground(originalBackgrounds.get(cellId));
    }

}
