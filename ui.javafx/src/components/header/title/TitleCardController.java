package components.header.title;

import components.main.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

import java.util.Objects;

public class TitleCardController {

    @FXML
    private ChoiceBox<String> styleChoiceBox;

    @FXML
    private ChoiceBox<String> animationsChoiceBox;

    private final String[] styles = {"No style", "Dark theme"};
    private final String[] animations = {"No animations", "Animation 1", "Animation 2", "Animation 3", "Animation 4"};

    private AppController appController;

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void initialize() {
        // Populate ChoiceBoxes
        styleChoiceBox.getItems().addAll(styles);
        animationsChoiceBox.getItems().addAll(animations);

        // Default selection
        styleChoiceBox.getSelectionModel().select(0);
        animationsChoiceBox.getSelectionModel().select(0);

        // Attach listener for when the selected item changes in the styleChoiceBox
        styleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                // Update the style in the AppController
                appController.setStyleOnParts(newValue);
            }
        });
    }

}




