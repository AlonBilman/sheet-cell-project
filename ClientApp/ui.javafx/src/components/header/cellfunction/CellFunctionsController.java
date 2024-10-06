package components.header.cellfunction;

import components.body.table.view.GridSheetController;
import components.main.AppController;
import dto.CellDataDTO;
import dto.sheetDTO;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
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
    public Label errorLabelText;
    @FXML
    private VBox inputDialog;
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

    private AppController appController;

    private String currCellShown;

    private ChangeListener<Number> currentScrollListener;

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

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
            dynamicButtonToggle(false, true);
            dynamicSliderToggle(true, true);
        } else {
            dynamicInputDialogToggle(true, false);
            dynamicButtonToggle(true, true);
            dynamicExitButtonToggle(true, false);
            dynamicSliderToggle(true, true);
        }
    }

    public void exitDynamicChangeListener() {
        exitDynamicChange();
    }

    public void exitDynamicChange() {
        showNumericButtons(false);
        //because the user will be still on the numeric cell.
        dynamicScroll.autosize();
        appController.exitDynamicChangeClicked();//enable all components
        showNumericButtons(true);
    }

    private void dynamicSliderToggle(boolean disable, boolean visible) {
        dynamicScroll.setVisible(visible);
        dynamicScroll.setDisable(disable);
    }

    private void dynamicExitButtonToggle(boolean disable, boolean visible) {
        exitDynamicChange.setDisable(disable);
        exitDynamicChange.setVisible(visible);
    }

    private void dynamicButtonToggle(boolean disable, boolean visible) {
        dynamicChangeButton.setDisable(disable);
        dynamicChangeButton.setVisible(visible);
    }

    private void dynamicInputDialogToggle(boolean disable, boolean visible) {
        inputDialog.setDisable(disable);
        inputDialog.setVisible(visible);
    }

    public void dynamicChangeButtonListener() {
        dynamicButtonToggle(true, false);
        dynamicInputDialogToggle(false, true);
        appController.dynamicChangeButtonClicked(); //disable all components
    }

    private void updateSlider(double fromValue, double toValue, double jumpValue) {
        dynamicScroll.setMin(fromValue);
        dynamicScroll.setMax(toValue);
        dynamicScroll.setBlockIncrement(jumpValue);
        dynamicScroll.setMajorTickUnit(jumpValue);
    }

    public void dynamicScrollListener() {
        dynamicScroll.valueProperty().addListener((observable, oldValue, newValue) -> {
            Double scrollValue = newValue.doubleValue();
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

    private boolean checkInputDialogValues(double fromValue, double toValue, double jumpValue) {
        if (fromValue >= toValue || jumpValue <= 0) {
            errorLabelText.setTextFill(Paint.valueOf("red"));
            errorLabelText.setText("ERROR! Ensure 'From' < 'To' and 'Jump' > 0.");
            clearDynamicChangeTextBoxes();
            return false;
        }
        if(jumpValue>toValue-fromValue) {
            errorLabelText.setTextFill(Paint.valueOf("red"));
            errorLabelText.setText("ERROR! Ensure 'jump' < ('To' - 'from').");
            return false;
        }
        return true;
    }

    public void sliderConfirmButtonListener() {
        try {
            double fromValue = Double.parseDouble(fromTextField.getText());
            double toValue = Double.parseDouble(toTextField.getText());
            double jumpValue = Double.parseDouble(jumpTextField.getText());

            if(!checkInputDialogValues(fromValue, toValue, jumpValue)) {
                clearDynamicChangeTextBoxes();
                return;
            }

            if (currentScrollListener != null)
                dynamicScroll.valueProperty().removeListener(currentScrollListener);

            errorLabelText.setText("");
            updateSlider(fromValue, toValue, jumpValue);

            currentScrollListener = (observable, oldValue, newValue) -> {
                double jumpedValue = Math.round((newValue.doubleValue() - fromValue) / jumpValue) * jumpValue + fromValue;
                dynamicScroll.setValue(jumpedValue);
                // Update displayed value and notify the AppController
                cellValueProperty.setText(String.valueOf(jumpedValue));
                appController.updateCellDynamically(currCellShown, String.valueOf(jumpedValue));
            };

            dynamicScroll.valueProperty().addListener(currentScrollListener);

            clearDynamicChangeTextBoxes();
            dynamicInputDialogToggle(true, false);
            dynamicSliderToggle(false, true);
            dynamicExitButtonToggle(false, true);

        } catch (NumberFormatException e) {
            errorLabelText.setTextFill(Paint.valueOf("red"));
            errorLabelText.setText("Please enter valid numeric values.");
            clearDynamicChangeTextBoxes();
        }
    }

    private void clearDynamicChangeTextBoxes() {
        fromTextField.clear();
        toTextField.clear();
        jumpTextField.clear();
    }

    public void dynamicCancelButtonListener() {
        appController.dynamicCancelClicked();
    }
}