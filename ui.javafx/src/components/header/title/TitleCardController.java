package components.header.title;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

public class TitleCardController {

    private AppController appController;

    public void setMainController(AppController mainController){
        this.appController = mainController;
    }

    @FXML
    private Text sheetCellText;

    @FXML
    private ChoiceBox<String> animationsChoiceBox;

    @FXML
    private ChoiceBox<String> styleChoiceBox;

    @FXML
    public void initialize() {
        // Initialization logic if needed
        System.out.println("TitleCardController initialized.");
    }

    // Add methods to handle ChoiceBox interactions if needed
}



