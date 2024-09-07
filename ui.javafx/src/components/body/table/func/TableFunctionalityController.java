package components.body.table.func;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TableFunctionalityController {

    private Boolean activeButtons;
    private AppController appController;

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

    public void setMainController(AppController mainController) {
        this.appController = mainController;
        setActiveButtons(false);
    }

    public void updateButtonStates() {
        boolean isEnabled = Boolean.TRUE.equals(activeButtons);
        setColButton.setDisable(!isEnabled);
        setRowButton.setDisable(!isEnabled);
        alignmentSetButton.setDisable(!isEnabled);
        cellStyleButton.setDisable(!isEnabled);
        addNewRangeButton.setDisable(!isEnabled);
        deleteExistingRangeButton.setDisable(!isEnabled);
        viewExistingRangeButton.setDisable(!isEnabled);
    }

    public void setActiveButtons(Boolean activeButtons) {
        this.activeButtons = activeButtons;
        updateButtonStates();
    }

    public void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void buildPopup(String title, String promptText, boolean isColumn) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        TextField inputField = new TextField();
        VBox vbox = createPopupVBox(inputField, popupStage, promptText, isColumn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 320, 150);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private VBox createPopupVBox(TextField inputField, Stage popupStage, String promptText, boolean isColumn) {
        Label promptLabel = new Label(promptText);
        Button confirmButton = new Button(isColumn ? "Set Width" : "Set Height");
        confirmButton.setOnAction(event -> handlePopupConfirm(inputField, popupStage, isColumn));
        return new VBox(20, promptLabel, inputField, confirmButton);
    }

    private void handlePopupConfirm(TextField inputField, Stage popupStage, boolean isColumn) {
        try {
            int newSize = Integer.parseInt(inputField.getText());
            if (newSize > 0) {
                // Notify AppController about the new size
                popupStage.close();
            } else {
                showInfoAlert("Error: Entered 0 or a negative value.");
            }
        } catch (NumberFormatException e) {
            showInfoAlert("Error: Entered value is not a number.");
        }
    }

    @FXML
    private void setColActionListener() {
        buildPopup("Enter New Column Width", "Please input new width:", true);
    }

    @FXML
    private void setRowActionListener() {
        buildPopup("Enter New Row Height", "Please input new height:", false);
    }

    @FXML
    private void alignmentSetListener() {
        System.out.println("Alignment Set button clicked");
    }

    @FXML
    private void cellStyleListener() {
        System.out.println("Cell Styling button clicked");
    }

    @FXML
    private void addNewRangeListener() {
        System.out.println("Add Range Set button clicked");
    }

    @FXML
    private void deleteExistingRangeListener() {
        System.out.println("Delete Range button clicked");
    }

    @FXML
    private void viewExistingRangeListener() {
        System.out.println("View Range button clicked");
    }
}
