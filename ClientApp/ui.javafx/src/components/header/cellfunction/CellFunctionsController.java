package components.header.cellfunction;

import components.body.table.view.GridSheetController;
import components.main.AppController;
import dto.CellDataDTO;
import dto.sheetDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class CellFunctionsController {

    @FXML
    public Button exitDynamicChange;
    @FXML
    public Button dynamicChangeButton;
    @FXML
    public Slider dynamicScroll;
    @FXML
    public TextField fromTextField;
    @FXML
    public TextField toTextField;
    @FXML
    public TextField jumpTextField;
    @FXML
    public Button sliderConfirm;
    @FXML
    public Button dynamicCancel;
    @FXML
    private AppController appController;
    @FXML
    private VBox inputDialog;

    @FXML
    private void showDialog() {
        inputDialog.setVisible(true);  // Show the dialog
    }

    @FXML
    private void hideDialog() {
        inputDialog.setVisible(false);  // Hide the dialog
    }
    
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

    @FXML
    private HBox cellFuncHBox;

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

    public void setTheme(String newTheme) {
        cellFuncHBox.getStylesheets().clear();
        String newStyle = "/components/header/cellfunction/cellFunctions" + newTheme + ".css";
        cellFuncHBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource(newStyle)).toExternalForm());
    }

    public void outOfFocus() {
        currCellShown = null;
        cellIdProperty.setText("Selected Cell Id");
        cellUpdatedProperty.setText("Cell Version");
        cellValueProperty.setText("Original Cell Value");
        newOriginalValText.setText("New Original Value");
        newOriginalValText.setDisable(true);
        updateCellButton.setDisable(true);
        showNumericButtons(false);
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

    public void showVersion(sheetDTO sheet, String versionNumber) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Table version number: " + versionNumber);
        FXMLLoader loader = new FXMLLoader();
        URL versionFXML = getClass().getResource("/components/body/table/view/gridSheetView.fxml");
        loader.setLocation(versionFXML);
        Parent root = loader.load();
        GridSheetController controller = loader.getController();
        controller.setMainController(appController);
        controller.populateTableView(sheet, true);
        controller.disableGridPane();
        Scene scene = new Scene(root, 800, 800);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void getVersionListener() {
        appController.getVersionClicked();
    }

    public void wakeVersionButton() {
        versionPickerButton.setDisable(false);
    }

    public void showNumericButtons(boolean bool) {
        if (bool) {
            dynamicChangeButton.setVisible(true);
            dynamicChangeButton.setDisable(false);
            dynamicScroll.setVisible(true);
            dynamicScroll.setDisable(true);
        } else {
            dynamicChangeButton.setDisable(true);
            exitDynamicChange.setDisable(true);
            dynamicScroll.setDisable(true);
            dynamicScroll.setVisible(false);
            exitDynamicChange.setVisible(false);
        }
    }

    public void exitDynamicChangeListener() {
        showNumericButtons(false);
        //because the user will be still on the numeric cell.
        dynamicScroll.setValue(1);
        appController.exitDynamicChangeClicked();//enable all components
        showNumericButtons(true);
    }

    public void dynamicChangeButtonListener() {
        dynamicScroll.setDisable(false);
        dynamicChangeButton.setDisable(true);
        dynamicChangeButton.setVisible(false);
        showDialog();
        //exitDynamicChange.setVisible(true);
        //exitDynamicChange.setDisable(false);
       // appController.dynamicChangeButtonClicked(); //disable all components
    }

    public void dynamicScrollListener() {
        dynamicScroll.valueProperty().addListener((observable, oldValue, newValue) -> {
            Integer scrollValue = newValue.intValue();
            String cellId = currCellShown;
            String newOriginalVal = String.valueOf(scrollValue);
            cellValueProperty.setText(newOriginalVal);
            appController.updateCellDynamically(cellId, newOriginalVal);
        });
    }


    public void setDynamicFuncDisable(boolean disable) {
        updateCellButton.setDisable(disable);
        versionPickerButton.setDisable(disable);
        newOriginalValText.setDisable(disable);
        cellUpdatedProperty.setDisable(disable);
    }

    public void submitInputAction(ActionEvent actionEvent) {
        //something
    }

    public void cancelInputAction(ActionEvent actionEvent) {
    }
}