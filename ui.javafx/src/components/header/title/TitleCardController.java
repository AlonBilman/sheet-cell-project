package components.header.title;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

public class TitleCardController {

    @FXML
    private ChoiceBox<String> animationsChoiceBox;

    @FXML
    private ChoiceBox<String> styleChoiceBox;


    private final String[] styles = {"No style", "Style 1", "Style 2", "Style 3", "Style 4"};
    private final String[] animations = {"No animations", "Animation 1", "Animation 2", "Animation 3", "Animation 4"};

    private AppController appController;

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void initialize() {
        styleChoiceBox.getItems().addAll(styles);
        animationsChoiceBox.getItems().addAll(animations);
        styleChoiceBox.getSelectionModel().select(0);
        animationsChoiceBox.getSelectionModel().select(0);
    }

    @FXML
    private Text sheetCellText;
}



