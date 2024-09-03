package components.header.loadFile;

import components.main.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.File;

public class LoadFileController {

    private AppController appController;
    @FXML
    private Button loadFileButton;

    @FXML
    private TextArea filePathText;

    public LoadFileController() {
        appController = new AppController();
    }

    public void showInfoAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("Error while loading file");
        alert.setContentText("Something went wrong when loading a file.");

        alert.showAndWait();
    }
    @FXML
    public void loadFileListener(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file - Allowed only .xml files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File file = fileChooser.showOpenDialog(loadFileButton.getScene().getWindow());
        //no file has been selected
        if (file == null) {
            return;
        }

        if(appController.checkFile(file))
            filePathText.setText(file.getAbsolutePath());
        else {
            showInfoAlert();
        }

        // Set the selected file's path to the TextArea






    }
}