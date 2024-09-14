package components.header.cellfunction;

import components.body.table.view.GridSheetController;
import components.main.AppController;
import dto.CellDataDTO;
import dto.sheetDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class CellFunctionsController {


    private AppController appController;

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    private String currCellShown;

    @FXML
    private Label cellIdProperty;

    @FXML
    private Label cellValueProperty;

    @FXML
    private Button updateCellButton;

    @FXML
    private Label cellUpdatedProperty;

    @FXML
    private Button versionPickerButton;

    @FXML
    private TextField newOriginalValText;

    public void showCell(CellDataDTO cell) {
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

    public void outOfFocus() {
        currCellShown = null;
        cellIdProperty.setText("Selected Cell Id");
        cellUpdatedProperty.setText("Cell Version");
        cellValueProperty.setText("Original Cell Value");
        newOriginalValText.setText("New Original Value");
        newOriginalValText.setDisable(true);
        updateCellButton.setDisable(true);
    }

    @FXML
    private void updateCellActionListener() {
        String newOriginalValue = newOriginalValText.getText();
        appController.updateCellClicked(currCellShown, newOriginalValue);
    }

    public String getCellIdFocused() {
        return currCellShown;
    }

    public void setFocus(String boarderId) {
        currCellShown = boarderId;
    }

    public void buildVersionPopup(int latestVersionNumber) {
        Stage popupStage = new Stage();
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(15));

        ToggleGroup versionGroup = new ToggleGroup();
        for (int i = 1; i <= latestVersionNumber; i++) {
            RadioButton radioButton = new RadioButton("Show version number: " + i);
            radioButton.setToggleGroup(versionGroup);
            radioButton.setUserData(i);
            vBox.getChildren().add(radioButton);

            radioButton.setOnAction(event -> {
                Integer selectedVersion = (Integer) versionGroup.getSelectedToggle().getUserData();
                appController.confirmVersionClicked(selectedVersion);
                popupStage.close();
            });
        }
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 300, 150);
        popupStage.setScene(scene);
        popupStage.setTitle("Select Version");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    public void showVersion(sheetDTO sheet, int versionNumber) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Table version number: " + versionNumber);
        FXMLLoader loader = new FXMLLoader();
        URL versionFXML = getClass().getResource("/components/body/table/view/gridSheetView.fxml");
        loader.setLocation(versionFXML);
        Parent root = loader.load();
        GridSheetController controller = loader.getController();
        controller.populateTableView(sheet, true);
        controller.disableGridPane();
        Scene scene = new Scene(root, 800, 800);
        stage.setScene(scene);
        stage.show();
    }

    public void getVersionListener() {
        appController.getVersionClicked();
    }

    public void wakeVersionButton() {
        versionPickerButton.setDisable(false);
    }
}