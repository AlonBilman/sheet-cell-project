package components.header.loadfile;

import components.main.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

import java.io.File;

public class LoadFileController {

    private AppController appController;

    @FXML
    private Button loadFileButton;

    @FXML
    private Label filePathLabel;

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void showInfoAlert(String problem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("!ERROR!");
        alert.setHeaderText("Error while loading file");
        alert.setContentText(problem);
        alert.showAndWait();
    }

    public void editFilePath(String filePath) {
        filePathLabel.setText(filePath);
    }

    @FXML
    public void loadFileListener(ActionEvent actionEvent) {
        appController.loadClicked();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file - Allowed only .xml files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File file = fileChooser.showOpenDialog(loadFileButton.getScene().getWindow());
        //in case of cancel or X
        if (file == null) {
            return;
        }
        appController.checkAndLoadFile(file);
    }
}
