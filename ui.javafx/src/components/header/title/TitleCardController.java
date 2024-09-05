package components.header.title;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class TitleCardController {
    @FXML ChoiceBox<String > animationsChoiceBox;
    @FXML ChoiceBox<String> styleChoiceBox;
    private final String[] styles = {"No style", "Style 1", "Style 2", "Style 3", "Style 4"};
    private final String[] animations = {"No animations", "Animation 1", "Animation 2", "Animation 3", "Animation 4"};
    public void initialize() {
        styleChoiceBox.getItems().addAll(styles);
        animationsChoiceBox.getItems().addAll(animations);

        styleChoiceBox.getSelectionModel().select(0);
        animationsChoiceBox.getSelectionModel().select(0);


    }
}
