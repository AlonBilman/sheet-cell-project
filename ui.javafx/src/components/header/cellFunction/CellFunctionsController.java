package components.header.cellFunction;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class CellFunctionsController {
 @FXML Button updateCellButton;
 @FXML TextArea cellValueProperty;
 @FXML TextArea cellIdProperty;
 @FXML TextArea cellUpdatedProperty;
 @FXML ChoiceBox<String> versionPickerChoiceBox;

 private String[] versions = {"Versions"};
    public void updateCellActionListener(ActionEvent actionEvent) {

        versionPickerChoiceBox.getItems().addAll(versions);
        versionPickerChoiceBox.getSelectionModel().select(0);
    }
}
