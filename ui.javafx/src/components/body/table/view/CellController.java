package components.body.table.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class CellController {

    @FXML private Label cellEffectiveValue;

    @FXML
    public void initialize() {
        cellEffectiveValue.setText("");
    }

    public void setCellEffectiveValue(String value) {
        this.cellEffectiveValue.setText(value);
    }

    @FXML
    private void cellSelectionListener(MouseEvent event){
        // Handle cell click
        System.out.println("Cell clicked with value: " + cellEffectiveValue.getText());
    }
}
