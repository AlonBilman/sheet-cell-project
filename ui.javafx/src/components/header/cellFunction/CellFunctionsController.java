package components.header.cellfunction;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class CellFunctionsController {

    private AppController appController;

    public void setMainController(AppController mainController){
        this.appController = mainController;
    }

    @FXML
    private Label cellIdProperty;

    @FXML
    private Label cellValueProperty;

    @FXML
    private Button updateCellButton;

    @FXML
    private Label cellUpdatedProperty;

    @FXML
    private ChoiceBox<String> versionPickerChoiceBox;

    @FXML
    public void initialize() {
        // Initialization logic if needed
        System.out.println("CellFunctionsController initialized.");
    }

    @FXML
    private void updateCellActionListener() {
        // Action listener logic
    }
}
