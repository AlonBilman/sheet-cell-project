package components.body.table.view;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Cell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class GridSheetController {

        @FXML private GridPane gridPane;

        private AppController appController;

        public void setMainController(AppController appController) {
                this.appController = appController;
        }

        public void initialize() {
                // Get dimensions from the engine
                int row = appController.getEngine().Display().getRowSize();
                int col = appController.getEngine().Display().getColSize();
                int rowHeight = appController.getEngine().Display().getRowHeight();
                int colWidth = appController.getEngine().Display().getColWidth();

                // Loop to add cells to the grid
                for (int i = 0; i < row; i++) {
                        for (int j = 0; j < col; j++) {

                                CellController cellController = new CellController();
                                if (i == 0) {
                                        cellController.setCellEffectiveValue(Character.toString((char) ('A' + j)));  // Sets 'A', 'B', etc.
                                }

                                // Create a new Pane (with a label inside) and size it
                                Pane cellPane = cellController.getCellPane();
                                cellPane.setPrefSize(colWidth, rowHeight);

                                // Add the pane to the grid
                                gridPane.add(cellPane, j, i);
                        }
                }
        }
}




