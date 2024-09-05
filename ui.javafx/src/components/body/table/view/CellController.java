package components.body.table.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class CellController {

    @FXML private Pane cellPane;
    @FXML private Label cellEffectiveValue;

    public void initialize() {
        // Ensure that the cellEffectiveValue is initialized
        cellEffectiveValue.setText("");
    }

    public Pane getCellPane() {
        return cellPane;
    }

    public Label getCellEffectiveValue() {
        return cellEffectiveValue;
    }

    public void setCellEffectiveValue(String value) {
        this.cellEffectiveValue.setText(value);
    }

}
