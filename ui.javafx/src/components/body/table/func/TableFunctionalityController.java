
package components.body.table.func;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class TableFunctionalityController {

    private AppController appController;

    public void setMainController(AppController mainController){
        this.appController = mainController;
    }

    @FXML
    private Button setColButton;

    @FXML
    private Button setRowButton;

    @FXML
    private Button alignmentSetButton;

    @FXML
    private Button cellStyleButton;

    @FXML
    private Button addNewRangeButton;

    @FXML
    private Button deleteExistingRangeButton;

    @FXML
    private Button viewExistingRangeButton;

    @FXML
    private void setColActionListener() {
        // Implement the action for the "Set Col Number" button
        System.out.println("Set Col Number button clicked");
    }

    @FXML
    private void setRowActionListener() {
        // Implement the action for the "Set Row Number" button
        System.out.println("Set Row Number button clicked");
    }

    @FXML
    private void AlignmentSetListener() {
        // Implement the action for the "Alignment Set" button
        System.out.println("Alignment Set button clicked");
    }

    @FXML
    private void cellStyleListener() {
        // Implement the action for the "Cell Styling" button
        System.out.println("Cell Styling button clicked");
    }

    @FXML
    private void addNewRangeListener() {
        // Implement the action for the "Add Range Set" button
        System.out.println("Add Range Set button clicked");
    }

    @FXML
    private void deleteExistingRangeListener() {
        // Implement the action for the "Delete Range" button
        System.out.println("Delete Range button clicked");
    }

    @FXML
    private void viewExistingRangeListener() {
        // Implement the action for the "View Range" button
        System.out.println("View Range button clicked");
    }
}
