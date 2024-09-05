package components.body.table.view;

import components.main.AppController;
import dto.sheetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

public class GridSheetController {

    @FXML
    private GridPane gridPane;

    private AppController appController;

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void updateTable(sheetDTO sheetCopy) {
        // Get dimensions from the DTO
        int row = sheetCopy.getRowSize();
        int col = sheetCopy.getColSize();
        int maxRow = sheetCopy.getRowHeight();
        int maxCol = sheetCopy.getColWidth();

        // Clear previous content from the grid (if updating the table)
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();

        // Set fixed size for columns and rows
        int fixedSize = 100; // 100 pixels

        // Add column constraints
        for (int j = 0; j < col; j++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setMinWidth(maxCol);
            colConstraints.setHalignment(javafx.geometry.HPos.CENTER); // Center alignment for column headers
            gridPane.getColumnConstraints().add(colConstraints);
        }

        // Add row constraints
        for (int i = 0; i < row; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(maxRow);
            rowConstraints.setValignment(javafx.geometry.VPos.CENTER); // Center alignment for row headers
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Loop to add cells to the grid
        for (int i = 0; i <= row; i++) {
            for (int j = 0; j <= col; j++) {
                Label cellLabel = new Label();
                if (i == 0 && j == 0) {
                    cellLabel.setText(" ");
                }
                // Set values for headers or other cells
                else if (i == 0) {
                    cellLabel.setText(Character.toString((char) ('A' + (j - 1))));  // Set column headers (A, B, C...)
                } else if (j == 0) {
                    cellLabel.setText(Integer.toString(i));  // Set row headers (1, 2, 3...)
                } else {
                    cellLabel.setText("-=--------------------------------");  // Placeholder for regular cells
                }

                // Set the size and alignment of the cell
                cellLabel.setPrefSize(maxRow*fixedSize, maxCol*fixedSize); // 100x100 pixels
                cellLabel.setAlignment(Pos.CENTER);  // Center align text

                // Set the initial background color
                cellLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));

                // Add a light border to the label
                cellLabel.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");

                // Set up mouse hover effects
                cellLabel.setOnMouseEntered((MouseEvent event) -> {
                    cellLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY.deriveColor(0, 1.0, 1.0, 0.5), CornerRadii.EMPTY, null)));
                });

                cellLabel.setOnMouseExited((MouseEvent event) -> {
                    cellLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
                });

                // Add the cell label to the grid at the correct position
                gridPane.add(cellLabel, j, i);
            }
        }
    }
}
