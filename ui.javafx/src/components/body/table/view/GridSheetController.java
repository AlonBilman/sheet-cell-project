package components.body.table.view;

import com.sun.jdi.BooleanType;
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

import java.util.Map;

public class GridSheetController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private GridPane gridPane;

    private AppController appController;

    public void initialize() {
        // Set fixed size for the GridPane
        gridPane.setPrefSize(600, 400);
        gridPane.setMinSize(600, 400);
        gridPane.setMaxSize(600, 400);
        // Ensure the ScrollPane reacts to the overflow
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void clearGridPane() {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }

    private void addBorders(int rows, int cols) {
        Label emptyLabel = new Label();
        gridPane.add(emptyLabel, 0, 0);

        for (int i = 1; i <= cols; i++) {
            Label cellLabel = new Label();
            cellLabel.setText(String.valueOf((char) ('A' + (i - 1))));
            cellLabel.setMinSize(10, 10);
            gridPane.add(cellLabel, i, 0);
        }
        for (int i = 1; i <= rows; i++) {
            Label cellLabel = new Label();
            cellLabel.setText(String.valueOf(i));
            cellLabel.setMinSize(10, 10);
            gridPane.add(cellLabel, 0, i);
        }
        //no need any functionality for them
        //for now.
    }

    public void updateTable(sheetDTO sheetCopy) {
        // Get dimensions from the DTO
        int row = sheetCopy.getRowSize();
        int col = sheetCopy.getColSize();
        int maxRow = sheetCopy.getRowHeight();
        int maxCol = sheetCopy.getColWidth();
        Map<String, CellDataDTO> cells = sheetCopy.getActiveCells();
        clearGridPane();
        addBorders(row, col);

        // Loop to add cells to the grid
        for (int i = 1; i <= row; i++) {
            for (int j = 1; j <= col; j++) {
                Label cellLabel = new Label();
                String id = String.valueOf((char) ('A' + (j - 1))) + i;
                CellDataDTO curr = cells.get(id);
                if (curr == null) {
                    cellLabel.setText("");
                } else {
                    //ASK AVIAD
                    if(curr.getEffectiveValue().getValue() instanceof Boolean) {
                        cellLabel.setText(curr.getEffectiveValue().getValue().toString().toUpperCase());
                    }
                    else cellLabel.setText(curr.getEffectiveValue().getValue().toString());
                }
                setCellFunctionality(cellLabel, maxCol, maxRow, id);
                gridPane.add(cellLabel, j, i);
            }
        }
    }

    private void setCellFunctionality(Label cellLabel, int maxRowHeight, int maxColWidth, String cellId) {
        cellLabel.setPrefSize(maxColWidth, maxRowHeight);
        cellLabel.setAlignment(Pos.CENTER);
        cellLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        cellLabel.setStyle("-fx-border-color: lightgray; -fx-border-width: 0.5;");
        cellLabel.setOnMouseEntered(event -> cellLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY.deriveColor(0, 1.0, 1.0, 0.5), CornerRadii.EMPTY, null))));
        cellLabel.setOnMouseExited(event -> cellLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null))));
        cellLabel.setOnMousePressed(event -> cellLabel.setStyle("-fx-border-color: red; -fx-border-width: 1;"));
        cellLabel.setOnMouseReleased(event -> cellLabel.setStyle("-fx-border-color: lightgray; -fx-border-width: 0.5;"));
        cellLabel.setOnMouseClicked(event -> appController.CellClicked(cellId));

    }
}
