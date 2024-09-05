package components.body.table.view;

import components.main.AppController;
import dto.sheetDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


import java.io.IOException;

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
        int rowHeight = sheetCopy.getRowHeight();
        int colWidth = sheetCopy.getColWidth();

        // Clear previous content from the grid (if updating the table)
       // gridPane.getChildren().clear();

        // Loop to add cells to the grid
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                try {
                    // Load the cell FXML
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("cell.fxml"));
                    Label cellPane = loader.load();

                    // Get the controller to set the cell's value
                    CellController cellController = loader.getController();

                    // Set values for headers or other cells
                    if (i == 0) {
                        cellController.setCellEffectiveValue(Character.toString((char) ('A' + j)));  // Set column headers (A, B, C...)
                    } else if (j == 0) {
                        cellController.setCellEffectiveValue(Integer.toString(i));  // Set row headers (1, 2, 3...)
                    } else {
                        cellController.setCellEffectiveValue(" ");  // Placeholder for regular cells
                    }

                    // Set the size of the cell
                    cellPane.setMinSize(colWidth, rowHeight);

                    // Add the cell pane to the grid at the correct position
                    gridPane.add(cellPane, j, i);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

