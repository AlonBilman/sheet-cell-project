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
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Objects;

public class LoadFileController {

    @FXML private HBox loadFileHBox;

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

    public void initialize() {
        progressBar.setVisible(false);
        progressBarPercentage.setVisible(false);
    }

    public void showInfoAlert(String problem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("!ERROR!");
        alert.setHeaderText("Error while loading file");
        alert.setContentText(problem);
        alert.setResizable(true);
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
        //in case of cancel or X
        if (file == null) {
            return;
        }
            appController.checkAndLoadFile(file);
    }

public void setTheme( String newTheme) {
        loadFileHBox.getStylesheets().clear();
        String newStyle = "/components/header/loadfile/loadFile" + newTheme + ".css";
        loadFileHBox.getStylesheets().add(Objects.requireNonNull(getClass().getResource(newStyle)).toExternalForm());
    }

    public void taskLoadingSimulation(Runnable callback) {
        progressBar.setVisible(true);
        progressBarPercentage.setVisible(true);
        Task<Boolean> loadFileTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                for (int i = 1; i <= 100; i++) {
                    Thread.sleep(8);
                    updateProgress(i, 100);
                    updateMessage(i + "%");
                }
                Thread.sleep(150);
                progressBar.setVisible(false);
                progressBarPercentage.setVisible(false);
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
