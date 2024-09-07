package components.header.cellfunction;

import components.main.AppController;
import dto.CellDataDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CellFunctionsController {


    private AppController appController;

    public void setMainController(AppController mainController){
        this.appController = mainController;
    }

    String currCellShown;

    @FXML
    private Label cellIdProperty;

    @FXML
    private Label cellValueProperty;

    @FXML
    private Button updateCellButton;

    @FXML
    private Label cellUpdatedProperty;

    @FXML
    public ComboBox<String> versionPickerComboBox;

    @FXML
    private TextField newOriginalValText;

    @FXML
    public void initialize() {
        // Initialization logic if needed
        System.out.println("CellFunctionsController initialized.");
    }

    public void showCell(CellDataDTO cell){
        currCellShown = cell.getId();
        cellIdProperty.setText(currCellShown);
        cellUpdatedProperty.setText(String.valueOf(cell.getLastChangeAt()));
        cellValueProperty.setText(cell.getOriginalValue());
        newOriginalValText.setDisable(false);
        newOriginalValText.setText("");
        updateCellButton.setDisable(false);
    }

    public void showInfoAlert(String problem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("!ERROR!");
        alert.setHeaderText("Error while updating A cell");
        alert.setContentText(problem);
        alert.showAndWait();
        outOfFocus();
    }

    public void outOfFocus(){
        currCellShown = null;
        cellIdProperty.setText("Selected Cell Id");
        cellUpdatedProperty.setText("Cell Version");
        cellValueProperty.setText("Original Cell Value");
        newOriginalValText.setText("New Original Value");
        newOriginalValText.setDisable(true);
        updateCellButton.setDisable(true);
        appController.cellOutOfFocus();
    }
    @FXML
    private void updateCellActionListener() {
        String newOriginalValue = newOriginalValText.getText();
        appController.updateCellClicked(currCellShown,newOriginalValue);
    }

    public String getCellIdFocused() {
        return currCellShown;
    }
}