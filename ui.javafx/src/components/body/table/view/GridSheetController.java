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
import java.util.Map;
import java.util.Set;

public class GridSheetController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    private AppController appController;

    Map<String, Label> labelMap;

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
            cellLabel.setMinSize(10, 10);
            gridPane.add(cellLabel, i, 0);
            makeClickVisuallyClicked(cellLabel);
        }
        for (int i = 1; i <= rows; i++) {
            Label cellLabel = new Label();
            cellLabel.setText(String.valueOf(i));
            cellLabel.setMinSize(10, 10);
            gridPane.add(cellLabel, 0, i);
            makeClickVisuallyClicked(cellLabel);
        }
        //no need any functionality for them
        //for now.
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
                    //getting the current Label to update (if needed)
                    //we already init the table, so we cellLabel not null.
                    cellLabel = getNodeFromGridPane(id);
                }
                //update the cell data
                updateCellLabel(cellLabel, cells.get(id));
            }
        }
    }

    private void updateCellLabel(Label cellLabel, CellDataDTO cellData) {
        if (cellData == null) {
            cellLabel.setText("");
        } else {
            Object value = cellData.getEffectiveValue().getValue();
            //I'm sorry... I couldn't think of another way...
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
        cellLabel.setOnMousePressed(event -> cellLabel.setStyle("-fx-border-color: red; -fx-border-width: 1;"));
        cellLabel.setOnMouseReleased(event -> cellLabel.setStyle("-fx-border-color: lightgray; -fx-border-width: 0.5;"));
    }

    private void setCellFunctionality(Label cellLabel, int maxRowHeight, int maxColWidth, String cellId) {
        cellLabel.setPrefSize(maxColWidth, maxRowHeight);
        cellLabel.setAlignment(Pos.CENTER);
        cellLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        cellLabel.setStyle("-fx-border-color: lightgray; -fx-border-width: 0.5;");
        final Background[] originalBackground = new Background[1];

        cellLabel.setOnMouseEntered(event -> {
            originalBackground[0] = cellLabel.getBackground();  //save the original background
            Color currentColor = ((Color) cellLabel.getBackground().getFills().get(0).getFill());
            Color hoverColor = currentColor.interpolate(Color.LIGHTGRAY, 0.5);
            cellLabel.setBackground(new Background(new BackgroundFill(hoverColor, CornerRadii.EMPTY, null)));
        });
        //Problem!
        cellLabel.setOnMouseExited(event -> {
            cellLabel.setBackground(originalBackground[0]);  // Restore the original background
        });

        makeClickVisuallyClicked(cellLabel);
        cellLabel.setOnMouseClicked(event -> appController.CellClicked(cellId));
    }


    public void colorizeImportantCells(sheetDTO curr, String id) {
        Map<String, CellDataDTO> cells = curr.getActiveCells();
        CellDataDTO cellData = cells.get(id); // won't be null
        Set<String> affectsOn = cellData.getAffectsOn();
        Set<String> depOn = cellData.getDependsOn();

        for (String dep : depOn) {
            Label label = labelMap.get(dep);
            if (!originalBackgrounds.containsKey(dep)) {
                originalBackgrounds.put(dep, label.getBackground());
            }
            label.setBackground(new Background(new BackgroundFill(Color.LIGHTSALMON, CornerRadii.EMPTY, null)));
        }

        for (String affect : affectsOn) {
            Label label = labelMap.get(affect);
            if (!originalBackgrounds.containsKey(affect)) {
                originalBackgrounds.put(affect, label.getBackground());
            }
            label.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, null)));
        }
    }

    public void returnOldColors() {
        for (String id : originalBackgrounds.keySet()) {
            Label label = labelMap.get(id);
            label.setBackground(originalBackgrounds.get(id));
        }
    }


}
