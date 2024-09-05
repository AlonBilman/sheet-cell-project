package components.body.table.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class CellController {

    @FXML private Label cellEffectiveValue;

    @FXML
    public void initialize() {
        // Ensure that the cellEffectiveValue is initialized
        cellEffectiveValue.setText("HELLO");
    }


    public Label getCellEffectiveValue() {
        return cellEffectiveValue;
    }

    public void setCellEffectiveValue(String value) {
        this.cellEffectiveValue.setText(value);
    }

    @FXML
    private void cellSelectionListener(MouseEvent event){
        return;
    }
}
