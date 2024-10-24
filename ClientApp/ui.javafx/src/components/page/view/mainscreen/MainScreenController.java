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
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

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
    public ScrollPane scrollPane;
    @FXML
    public TitledPane historyTab;
    @FXML
    public TableView<Permission> historyTable;
    @FXML
    public TableColumn<Permission, String> HistoryUserNameColumn;
    @FXML
    public TableColumn<Permission, String> HistoryPermissionNameColumn;
    @FXML
    public TableColumn<Permission, String> HistoryPermissionApprovedColumn;

    @FXML
    private TableView<Permission> SheetPermissionTable;
    @FXML
    private TableColumn<Permission, String> userNameColumn;
    @FXML
    private TableColumn<Permission, String> permissionNameColumn;
    @FXML
    private TableColumn<Permission, String> permissionApprovedColumn;
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
    @FXML
    private ChoiceBox<String> styleChoiceBox;

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
    private final String[] styles = {"Default theme", "Theme 1", "Theme 2"};
    private String themeStyle = "Default theme";

    @FXML
    public void initialize() {
        userUploadedColumn.setCellValueFactory(new PropertyValueFactory<>("UserUploaded"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("SheetName"));
        sheetSizeColumn.setCellValueFactory(new PropertyValueFactory<>("SheetSize"));

        HistoryUserNameColumn.setCellValueFactory(new PropertyValueFactory<>("UserName"));
        HistoryPermissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("PermissionType"));
        HistoryPermissionApprovedColumn.setCellValueFactory(new PropertyValueFactory<>("ApprovedPermission"));

        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("UserName"));
        permissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("PermissionType"));
        permissionApprovedColumn.setCellValueFactory(new PropertyValueFactory<>("ApprovedPermission"));
        disableAcceptAndDenyButtons(true);
        disableViewAndRequestButtons(true);
        styleChoiceBox.getItems().addAll(styles);
        styleChoiceBox.getSelectionModel().select(0);

        styleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                setTheme(newValue);
            }
        });

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

    private void setTheme(String newValue) {
        String cssFilePath = "";

        switch (newValue) {
            case "Default theme":
                cssFilePath = "mainScreenDefault.css"; // Update this with your actual path
                break;
            case "Theme 1":
                cssFilePath = "mainScreenTheme1.css"; // Update this with your actual path
                break;
            case "Theme 2":
                cssFilePath = "mainScreenTheme2.css"; // Update this with your actual path
                break;
            default:
                break;
        }

        themeStyle = newValue;
        scrollPane.getStylesheets().clear();
        scrollPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(cssFilePath))
                        .toExternalForm()
        );
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
                    Type listType = new TypeToken<PermissionData>() {
                    }.getType();
                    PermissionData permissionData = GSON.fromJson(response.body().string(), listType);
                    updatePermissionListFromList(permissionData.getPermissions());
                    updateHistoryPermissionList(permissionData.getHistory());
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

    private void updateHistoryPermissionList(List<Permission> history) {
        Platform.runLater(() -> {
            ObservableList<Permission> currentData = historyTable.getItems();
            currentData.clear();
            if (history != null)
                currentData.addAll(history);
            historyTable.refresh();
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

    private void updatePermissionListFromList(List<Permission> data) {
        Platform.runLater(() -> {
            ObservableList<Permission> currentData = SheetPermissionTable.getItems();
            currentData.clear();
            if (data != null)
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

        ObservableList<Permission> permissionDataList = SheetPermissionTable.getItems();

        Permission userPermissionData = permissionDataList.stream()
                .filter(permission -> permission.getUserName().equalsIgnoreCase(username))
                .findAny()
                .orElse(null);
        if (userPermissionData == null || !userPermissionData.getApprovedPermission().equalsIgnoreCase("yes")) {
            showInfoAlert("You don't have permission for this sheet.");
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
            appController.setSheet(sheetName, this::error, true);
            appController.setUserName(username);
            appController.setStyleOnParts(themeStyle);
            if (isReader)
                appController.setReaderAccess();
            //save the main exit window
            EventHandler<WindowEvent> existingHandler = stage.getOnCloseRequest();
            stage.setOnCloseRequest(event -> {
                appController.stopRefresher();
                //calling the main window closing
                if (existingHandler != null) {
                    existingHandler.handle(event);
                }
                stage.close();
            });
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