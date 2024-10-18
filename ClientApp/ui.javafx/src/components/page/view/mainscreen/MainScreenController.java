package components.page.view.mainscreen;

import components.page.view.sheetscreen.AppController;
import http.CallerService;
import http.HttpClientUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Timer;

import static constants.Constants.REFRESH_RATE;


public class MainScreenController {

    public Stage stage;
    @FXML
    public static AnchorPane anchorPane;
    @FXML
    public SubScene permissionSubScene;
    public Label sheetNames;

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

    public void setTheme(String value) {
        anchorPane.getStylesheets().clear();
        anchorPane.getStylesheets().add(
                Objects.requireNonNull(MainScreenController.class.getResource("components/page/view/mainscreen/" + value + ".css"))
                        .toExternalForm()
        );
    }

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
        alert.setHeaderText("Error :");
        alert.setContentText(problem);
        alert.setResizable(true);
        alert.showAndWait();
    }

    public void ViewSheetListener(ActionEvent actionEvent) {
        initAppScreen(SheetName);
    }

    @FXML
    public void RequestPermissionListener(ActionEvent actionEvent) {
        // Show the permission subscene
        if (SheetName != null) {
            showPermissionPopup();
            if (permissionName != null) {
                showTimedNotification(SheetName,5);
            }
        }
    }

    private void handlePermissionRequest() {
        // Implement the logic to request permission here
        System.out.println("Permission request sent for sheet: " + SheetName + " by user: " + username);
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

    private void showPermissionPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);

        // Create radio buttons for permissions
        ToggleGroup permissionGroup = new ToggleGroup();
        RadioButton ownerButton = new RadioButton("OWNER");
        RadioButton editorButton = new RadioButton("EDITOR");
        RadioButton readerButton = new RadioButton("READER");
        RadioButton noneButton = new RadioButton("NONE");


        ownerButton.setToggleGroup(permissionGroup);
        editorButton.setToggleGroup(permissionGroup);
        readerButton.setToggleGroup(permissionGroup);
        noneButton.setToggleGroup(permissionGroup);

        // Default selection (optional)
        noneButton.setSelected(true);

        // Create a confirm button
        Button confirmButton = new Button("Confirm");

        // Handle confirm button action
        confirmButton.setOnAction((ActionEvent event) -> {
            permissionName = null;
            RadioButton selectedRadioButton = (RadioButton) permissionGroup.getSelectedToggle();
            String selectedPermission = selectedRadioButton.getText();
            permissionName = selectedPermission;
            System.out.println("Selected Permission: " + selectedPermission);

            // Here you can return or process the selectedPermission as needed
            popupStage.close(); // Close popup window
        });

        // Arrange the layout
        VBox layout = new VBox(10, readerButton, ownerButton, editorButton, noneButton, confirmButton);
        layout.setPadding(new Insets(20));
        Scene popupScene = new Scene(layout, 300, 200);

        popupStage.setTitle("Select Permission");
        popupStage.setScene(popupScene);
        popupStage.showAndWait();
    }

    public void showTimedNotification(String Sheetname, int durationInSeconds) {
        // Set the message and show the label
        sheetNames.setText(Sheetname);
       permissionSubScene.setVisible(true);

        // Hide the notification after the specified duration
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(durationInSeconds), evt -> permissionSubScene.setVisible(false)));
        timeline.play();
    }
}