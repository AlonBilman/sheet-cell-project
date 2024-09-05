package components.body.table.view;

import components.main.AppController;
import dto.sheetDTO;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class GridSheetController {

        @FXML Pane cell;
        @FXML GridPane gridPane;
        @FXML private CellController cellController;

        private AppController appController;

        public void setMainController(AppController mainController){
                this.appController = mainController;
        }

        public void listenerToLoadFile(sheetDTO sheetCopy) {
                // Get dimensions from the engine
                int row = sheetCopy .getRowSize();
                int col = sheetCopy.getColSize();
                int rowHeight = sheetCopy.getRowHeight();
                int colWidth = sheetCopy.getColWidth();

                // Loop to add cells to the grid
                for (int i = 0; i < row; i++) {
                        for (int j = 0; j < col; j++) {

                                CellController cellController = new CellController();
                                if (i == 0) {
                                        cellController.setCellEffectiveValue(Character.toString((char) ('A' + j)));  // Sets 'A', 'B', etc.
                                }

                                // Create a new Pane (with a label inside) and size it
                                cell.setPrefSize(colWidth, rowHeight);

                                // Add the pane to the grid
                                gridPane.add(cell, j, i);
                        }
                }
        }
}




