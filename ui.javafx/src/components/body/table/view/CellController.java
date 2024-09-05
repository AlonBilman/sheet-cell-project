package components.body.table.view;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class CellController {

    @FXML private Label cellEffectiveValue;

    private AppController appController;

    public void setMainController(AppController mainController){
        this.appController = mainController;
    }

    @FXML
    public void initialize() {
        cellEffectiveValue.setText("");
    }

    public Label getCellEffectiveValue() {
        return cellEffectiveValue;
    }

    public void setCellEffectiveValue(String value) {
        this.cellEffectiveValue.setText(value);
    }

    @FXML
    private void cellSelectionListener(MouseEvent event){
        //when someone clicks
        return;
    }
}
