package components.page.view.mainscreen;

import com.google.gson.reflect.TypeToken;
import components.page.view.sheetscreen.AppController;
import http.CallerService;
import http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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
    public static AnchorPane anchorPane;
    @FXML
    public SubScene permissionSubScene;
    @FXML
    public Label sheetNames;
    @FXML
    public Label userNameText;
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
    private Button RequestPermissionButton;
    private String username;
    private String sheetName;
    private String tableUsername;
    private String owner;
    private String permissionName;
    private String permissionPicked;
    private String permissionApproved;
    private Timer timer;
    private UsersRefresher usersRefresher;
    private BooleanProperty autoUpdate;
    private Map<String, String> query;
    private CallerService httpCallerService;

    @FXML
    public void initialize() {
        userUploadedColumn.setCellValueFactory(new PropertyValueFactory<>("UserUploaded"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("SheetName"));
        sheetSizeColumn.setCellValueFactory(new PropertyValueFactory<>("SheetSize"));

        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("UserName"));
        permissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("PermissionType"));
        permissionApprovedColumn.setCellValueFactory(new PropertyValueFactory<>("ApprovedPermission"));
        disableAcceptAndDenyButtons(true);
        disableViewAndRequestButtons(true);

        SheetTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.equals(oldValue)) {
                    disableViewAndRequestButtons(false);
                    updateList();
                }
            } else {
                disableViewAndRequestButtons(true);
            }
        });

        SheetTable.setOnMouseClicked(event -> {
            AppUser selectedItem = SheetTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                updateList();
            }
        });

        SheetPermissionTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                permissionName = newValue.getPermissionType();
                tableUsername = newValue.getUserName();
                permissionApproved = newValue.getApprovedPermission();
                disableAcceptAndDenyButtons(false);
            } else {
                disableAcceptAndDenyButtons(true);
            }
        });

        if (SheetTable.getSelectionModel().getSelectedItem() != null) {
            ViewSheetButton.setDisable(true);
        }

        httpCallerService = new CallerService();
        autoUpdate = new SimpleBooleanProperty(true);
        query = new HashMap<>();
        startListRefresher();
    }

    private void updateList() {
        sheetName = SheetTable.getSelectionModel().getSelectedItem().getSheetName();
        owner = SheetTable.getSelectionModel().getSelectedItem().getUserUploaded();
        updatePermissionList(sheetName, owner);
    }

    private void disableViewAndRequestButtons(boolean toDisable) {
        ViewSheetButton.setDisable(toDisable);
        RequestPermissionButton.setDisable(toDisable);
    }

    private void disableAcceptAndDenyButtons(boolean toDisable) {
        DenyPermissionButton.setDisable(toDisable);
        AcceptPermissionButton.setDisable(toDisable);
    }

    private void updatePermissionList(String sheetName, String owner) {
        query.clear();
        query.put(SHEET_ID, sheetName);
        query.put(OWNER, owner);
        httpCallerService.getPermissions(query, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    httpCallerService.handleErrorResponse(response);
                    Type listType = new TypeToken<List<PermissionData>>() {
                    }.getType();
                    List<PermissionData> permissionsList = GSON.fromJson(response.body().string(), listType);
                    updatePermissionListFromList(permissionsList);

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showInfoAlert(e.getMessage());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
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

    private void clearTableSelection() {
        SheetTable.getSelectionModel().clearSelection();
        updatePermissionListFromList(null);//clear second table view
    }

    public void ViewSheetListener() {
        clearTableSelection();
        initAppScreen(sheetName);
    }

    @FXML
    public void RequestPermissionListener() {
        if (sheetName != null)
            showPermissionPopup();
        if (permissionPicked == null)
            return;

        query.clear();
        query.put(SHEET_ID, sheetName);
        query.put(OWNER, owner);

        httpCallerService.requestPermission(query, permissionPicked, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    showInfoAlert(e.getMessage());
                    clearTableSelection();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    httpCallerService.handleErrorResponse(response);
                    Platform.runLater(() -> {
                        updatePermissionList(sheetName, owner);
                        clearTableSelection();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showInfoAlert(e.getMessage());
                        clearTableSelection();
                    });
                }
            }
        });
    }

    public void AcceptPermissionListener() {
        if (SheetPermissionTable.getSelectionModel().getSelectedItem() != null) {
            SheetPermissionTable.getSelectionModel().clearSelection();
        }
        answerPermissionRequest("yes");
    }

    public void answerPermissionRequest(String answer) {
        query.clear();
        query.put(SHEET_ID, sheetName);
        query.put(REQUESTER, tableUsername);
        httpCallerService.acceptOrDenyPermission(query, answer, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    showInfoAlert(e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    httpCallerService.handleErrorResponse(response);
                    Platform.runLater(() -> {
                        updatePermissionList(sheetName, owner);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showInfoAlert(e.getMessage());
                    });
                }
            }
        });
    }

    public void DenyPermissionListener() {
        if (SheetPermissionTable.getSelectionModel().getSelectedItem() != null) {
            SheetPermissionTable.getSelectionModel().clearSelection();
        }
        answerPermissionRequest("no");
    }

    public void LoadFileListener() {
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

    private void updatePermissionListFromList(List<PermissionData> data) {
        Platform.runLater(() -> {
            ObservableList<PermissionData> currentData = SheetPermissionTable.getItems();
            currentData.clear();
            if(data!=null)
                currentData.addAll(data);
            SheetPermissionTable.refresh();
        });
    }

    private void updateUsersList(List<AppUser> users) {
        Platform.runLater(() -> {
            ObservableList<AppUser> currentData = SheetTable.getItems();
            ObservableList<AppUser> newData = FXCollections.observableArrayList(users);
            if (!currentData.equals(newData)) {
                currentData.clear();
                currentData.addAll(newData);
                SheetTable.refresh();
            }
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

    public void initAppScreen(String sheetName) {
        if (sheetName == null) {
            showInfoAlert("Please select a sheet to view");
            return;
        }

        ObservableList<PermissionData> permissionDataList = SheetPermissionTable.getItems();

        PermissionData userPermissionData = permissionDataList.stream()
                .filter(permission -> permission.getUserName().equalsIgnoreCase(username))
                .findAny()
                .orElse(null);
        if (userPermissionData == null || !userPermissionData.getApprovedPermission().equalsIgnoreCase("yes")) {
            showInfoAlert("You dont have permission for this sheet.");
            return;
        }

        try {
            stopListRefresher();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../sheetscreen/app.fxml"));
            Parent root = loader.load();
            boolean isReader = userPermissionData.getPermissionType().equalsIgnoreCase("reader");
            Scene scene = new Scene(root, 1120, 800);
            stage.setScene(scene);
            stage.setTitle("Sheet Cell - App Screen");
            AppController appController = loader.getController();
            appController.setStage(stage);
            appController.setLoadFile(sheetName, this::error);
            appController.setUserName(username);
            if (isReader)
                appController.setReaderAccess();

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

    private void showPermissionPopup() {
        permissionPicked = null;
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);

        ToggleGroup permissionGroup = new ToggleGroup();
        RadioButton editorButton = new RadioButton("WRITER");
        RadioButton readerButton = new RadioButton("READER");

        editorButton.setToggleGroup(permissionGroup);
        readerButton.setToggleGroup(permissionGroup);

        Button confirmButton = new Button("Confirm");

        confirmButton.setOnAction((ActionEvent event) -> {
            RadioButton selectedRadioButton = (RadioButton) permissionGroup.getSelectedToggle();
            if (selectedRadioButton != null) {
                String selectedPermission = selectedRadioButton.getText();
                permissionPicked = selectedPermission;
                System.out.println("Selected Permission: " + selectedPermission);
                popupStage.close();
            }
        });

        VBox layout = new VBox(10, readerButton, editorButton, confirmButton);
        layout.setPadding(new Insets(20));
        Scene popupScene = new Scene(layout, 300, 200);

        popupStage.setTitle("Select Permission");
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    public void setUserName(String userName) {
        this.username = userName;
        userNameText.setText(userName);
    }
}