package components.header.title;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;


public class TitleCardController {

    @FXML
    private ChoiceBox<String> styleChoiceBox;

    @FXML
    private ChoiceBox<String> animationsChoiceBox;

    private final String[] styles = {"No style"};
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

}




