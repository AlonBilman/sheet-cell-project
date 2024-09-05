package components.header.title;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

public class TitleCardController {
    @FXML ChoiceBox<String > animationsChoiceBox;
    @FXML ChoiceBox<String> styleChoiceBox;
    private final String[] styles = {"No style", "Style 1", "Style 2", "Style 3", "Style 4"};
    private final String[] animations = {"No animations", "Animation 1", "Animation 2", "Animation 3", "Animation 4"};
    public void initialize() {
        styleChoiceBox.getItems().addAll(styles);
        animationsChoiceBox.getItems().addAll(animations);

    private AppController appController;
    styleChoiceBox.getSelectionModel().select(0);
    animationsChoiceBox.getSelectionModel().select(0);

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



