package components.page.view.mainscreen;

import components.page.view.sheetscreen.AppController;
import http.CallerService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.function.Consumer;

import static constants.Constants.REFRESH_RATE;


public class MainScreenController {

    public Stage stage;

    @FXML
    private TableView<PermissionData> SheetPermissionTable;
    @FXML
    private TableColumn<PermissionData, String> userNameColumn;
    @FXML
    private TableColumn<PermissionData, String> permissionNameColumn;
    @FXML
    private TableColumn<PermissionData, String> permissionApprovedColumn;
    @FXML
    private TableView<AppUser> SheetTable;
    @FXML
    private TableColumn<AppUser, String> userUploadedColumn;
    @FXML
    private TableColumn<AppUser, String> sheetNameColumn;
    @FXML
    private TableColumn<AppUser, String> sheetSizeColumn;

    @FXML
    public Button LoadSheetButton;

    @FXML
    public Button ViewSheetButton;

    @FXML
    public Button DenyPermissionButton;

    @FXML
    public Button AcceptPermissionButton;

    @FXML
    public Button RequestPermissionButton;

    public String SheetName;
    public String username;
    public String permissionName;
    public Timer timer;
    public UsersRefresher usersRefresher;
    public BooleanProperty autoUpdate;


    String url = "http://LocalHost:8080/SheetCell";

    CallerService httpCallerService;

    @FXML
    public void initialize() {
        // Bind columns to the SheetData properties
        userUploadedColumn.setCellValueFactory(new PropertyValueFactory<>("userUploaded"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("SheetName"));
        sheetSizeColumn.setCellValueFactory(new PropertyValueFactory<>("sheetSize"));

        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        permissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("permissionName"));
        permissionApprovedColumn.setCellValueFactory(new PropertyValueFactory<>("permissionApproved"));

        //do we change this to http request to
        // Add listener to get selected row's sheet name
        SheetTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                SheetName = newValue.getSheetName();
            }
        });

        SheetPermissionTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                permissionName = newValue.getPermissionType();
                username = newValue.getUserName();
            }
        });

        httpCallerService = new CallerService();
        autoUpdate = new SimpleBooleanProperty(true);
        startListRefresher();
    }

    public void showInfoAlert(String problem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("!ERROR!");
        alert.setHeaderText("Error while loading file");
        alert.setContentText(problem);
        alert.setResizable(true);
        alert.showAndWait();
    }

    public void setTheme(String newTheme) {

    }

    public void ViewSheetListener(ActionEvent actionEvent) {
        openAppScreen();
    }

    public void RequestPermissionListener(ActionEvent actionEvent) {
        //get user that requested
        //get permission that he asked for
        //add it to the specific sheet's table view
    }

    public void AcceptPermissionListener(ActionEvent actionEvent) {
        //get user that requested
        //get permission that he asked for
        //add it to the user
    }

    public void DenyPermissionListener(ActionEvent actionEvent) {
        //get user that requested
        //get permission that he asked for
        //remove it from the table?
    }

    public void addSheetToTableView(String username, String name, String sheetSize) {
        ObservableList<AppUser> currentData = SheetTable.getItems();
        currentData.add(new AppUser(username, name, sheetSize));
        SheetTable.refresh();
    }

    public void addPermissionToTable(String username, String permission, String isApproved) {
        ObservableList<PermissionData> currentData = SheetPermissionTable.getItems();
        currentData.add(new PermissionData(username, permission, isApproved));
        SheetPermissionTable.refresh();
    }

    public void LoadFileListener(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file - Allowed only .xml files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File file = fileChooser.showOpenDialog(LoadSheetButton.getScene().getWindow());
        //in case of cancel or X
        if (file == null) {
            return;
        }
        try {
            checkAndLoadFile(file);
        } catch (IOException e) {
            showInfoAlert(e.getMessage());
        }
    }

    private void checkAndLoadFile(File file) throws IOException {
        httpCallerService.uploadFileAsync(file, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    showInfoAlert("File upload failed. " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    httpCallerService.handleErrorResponse(response);
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showInfoAlert("File upload failed. " + e.getMessage());
                    });
                }

            }
        });
    }

    private void updateUsersList(List<AppUser> users) {
        Platform.runLater(() -> {
            ObservableList<AppUser> currentData = SheetTable.getItems();
            currentData.clear();
            currentData.addAll(users);
            SheetTable.refresh();
        });
    }

    public void startListRefresher() {
        usersRefresher = new UsersRefresher(
                autoUpdate,
                this::updateUsersList);
        timer = new Timer();
        timer.schedule(usersRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void openAppScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../sheetscreen/app.fxml")); // Update the path accordingly
            Parent root = loader.load();

            // Create a new scene and set it to the current stage
            Scene scene = new Scene(root, 1120, 800);
            stage.setScene(scene);
            stage.setTitle("Sheet Cell - App Screen");

            // Pass the stage to the app controller for navigation back
            AppController appController = loader.getController();
            appController.setStage(stage);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

