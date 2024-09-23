package components.header.loadfile;

import components.main.AppController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import java.io.File;

public class LoadFileController {

    private AppController appController;

    @FXML
    private Button loadFileButton;

    @FXML
    private Label filePathLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressBarPercentage;

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
    public void loadFileListener() {
        appController.loadClicked();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file - Allowed only .xml files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File file = fileChooser.showOpenDialog(loadFileButton.getScene().getWindow());
        if (file == null) {
            return;
        }
            appController.checkAndLoadFile(file);
    }

//need to ask aviad
    public void taskLoadingSimulation(Runnable callback) {
        Task<Boolean> loadFileTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                for (int i = 1; i <= 100; i++) {
                    Thread.sleep(6);
                    updateProgress(i, 100);
                    updateMessage(i + "%");
                }
                updateMessage("Finished Loading");
                return true;
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(callback);
            }
        };
        progressBar.progressProperty().bind(loadFileTask.progressProperty());
        progressBarPercentage.textProperty().bind(loadFileTask.messageProperty());
        new Thread(loadFileTask).start();
    }
}
