package components.page.view.mainscreen;

import com.google.gson.reflect.TypeToken;
import components.page.view.sheetscreen.AppController;
import http.CallerService;
import http.HttpClientUtil;
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
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static constants.Constants.*;

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

    public String sheetName;
    public String username;
    public String permissionName;
    public String permissionApproved;
    public Timer timer;
    public UsersRefresher usersRefresher;
    public BooleanProperty autoUpdate;

    CallerService httpCallerService;

    @FXML
    public void initialize() {
        // Bind columns to the SheetData properties
        userUploadedColumn.setCellValueFactory(new PropertyValueFactory<>("UserUploaded"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("SheetName"));
        sheetSizeColumn.setCellValueFactory(new PropertyValueFactory<>("SheetSize"));

        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("UserName"));
        permissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("PermissionType"));
        permissionApprovedColumn.setCellValueFactory(new PropertyValueFactory<>("ApprovedPermission"));
        // Add listener to get selected row's sheet name
        SheetTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                sheetName = newValue.getSheetName();
                String owner = newValue.getUserUploaded();
                updatePermissionList(sheetName, owner);
            }
        });

        SheetPermissionTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                permissionName = newValue.getPermissionType();
                username = newValue.getUserName();
                permissionApproved = newValue.getApprovedPermission();
            }
        });

        httpCallerService = new CallerService();
        autoUpdate = new SimpleBooleanProperty(true);
        startListRefresher();
    }

    private void updatePermissionList(String sheetName, String owner) {
        Map<String, String> query = new HashMap<>();
        query.put(SHEET_ID, sheetName);
        query.put(OWNER, owner);
        httpCallerService.getPermissions(query, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try{
                    httpCallerService.handleErrorResponse(response);
                    Type listType = new TypeToken<List<PermissionData>>() {}.getType();
                    List<PermissionData> permissionsList = GSON.fromJson(response.body().string(), listType);
                    updatePermissionList(permissionsList);

                }catch (Exception e){
                    Platform.runLater(()->{
                            showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(()->{
                    showInfoAlert(e.getMessage());
                });
            }
        });

    }

    public void showInfoAlert(String problem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("!ERROR!");
        alert.setHeaderText("Error :");
        alert.setContentText(problem);
        alert.setResizable(true);
        alert.showAndWait();
    }

    public void ViewSheetListener(ActionEvent actionEvent) {
        initAppScreen(sheetName);
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

    private void updatePermissionList(List<PermissionData> data) {
        Platform.runLater(()->{
            ObservableList<PermissionData> currentData = SheetPermissionTable.getItems();
            currentData.clear();
            currentData.addAll(data);
            SheetPermissionTable.refresh();
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
        timer.schedule(usersRefresher, 0, REFRESH_RATE);
    }

    public void stopListRefresher() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (usersRefresher != null) {
            usersRefresher.cancel();
            usersRefresher = null;
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest((event) -> {
            event.consume();
            disconnect();
        });
    }

    private void disconnect() {
        stopListRefresher();
        HttpClientUtil.shutdown();
        stage.close();
    }

    public void initAppScreen(String name) {
        if (name == null) {
            showInfoAlert("please select a sheet to view");
            return;
        }
        try {
            stopListRefresher();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../sheetscreen/app.fxml")); // Update the path accordingly
            Parent root = loader.load();
            Scene scene = new Scene(root, 1120, 800);
            stage.setScene(scene);
            stage.setTitle("Sheet Cell - App Screen");
            AppController appController = loader.getController();
            appController.setStage(stage);
            appController.setLoadFile(name, this::error);
            stage.show();
        } catch (Exception e) {
            showInfoAlert(e.getMessage());
        }
    }

    private void error(Exception e) {
        Platform.runLater(() -> {
            showInfoAlert(e.getMessage());
        });
    }
}