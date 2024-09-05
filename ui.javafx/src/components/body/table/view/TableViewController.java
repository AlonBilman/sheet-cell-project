
package components.body.table.view;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class TableViewController {

    private AppController appController;

    public void setMainController(AppController mainController){
        this.appController = mainController;
    }

    @FXML
    private TableView<?> tableSheet; // Assuming a generic type. Replace '?' with the actual data type if known.

    @FXML
    private TableColumn<?, ?> column1; // Replace '?' with the actual data type if known.

    @FXML
    private TableColumn<?, ?> column2; // Replace '?' with the actual data type if known.

    @FXML
    public void initialize() {
        // Initialization logic if needed
        System.out.println("TableViewController initialized.");
    }
}