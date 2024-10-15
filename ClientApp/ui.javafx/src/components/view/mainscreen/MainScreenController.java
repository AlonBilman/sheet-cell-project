package components.view.mainscreen;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class MainScreenController {

    @FXML
    private TableView<PermissionData> SheetPermissionTable;
    @FXML
    private TableColumn<PermissionData, String> userNameColumn;
    @FXML
    private TableColumn<PermissionData, String> permissionNameColumn;
    @FXML
    private TableColumn<PermissionData, String> permissionApprovedColumn;
    @FXML
    private TableView<SheetTableData> SheetTable;
    @FXML
    private TableColumn<SheetTableData, String> userUploadedColumn;
    @FXML
    private TableColumn<SheetTableData, String> sheetNameColumn;
    @FXML
    private TableColumn<SheetTableData, String> sheetSizeColumn;

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

     String url = "http://LocalHost:8080/SheetCell";


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
    }

    public void ViewSheetListener(ActionEvent actionEvent) {
            if(SheetName != null) {
            //  sendSheetNameToServer(SheetName);
                //
                //ask the server for this sheet
                // if we got it. continue to the 3rd screen
                // else -> show an error.
        } else {
            System.err.println("No sheet selected.");

        }
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
        ObservableList<SheetTableData> currentData = SheetTable.getItems();
        currentData.add(new SheetTableData(username, name, sheetSize));
        SheetTable.refresh();
    }

    public void addPermissionToTable(String username, String permission, String isApproved) {
        ObservableList<PermissionData> currentData = SheetPermissionTable.getItems();
        currentData.add(new PermissionData(username, permission, isApproved));
        SheetPermissionTable.refresh();
    }

}
