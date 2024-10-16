package components.header.title;

import components.page.view.sheetscreen.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Objects;

public class TitleCardController {

    @FXML
    public Text sheetCellText;
    @FXML
    public Button backToMainScreenButton;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ChoiceBox<String> styleChoiceBox;
    @FXML
    private ChoiceBox<String> animationsChoiceBox;

    private final String[] styles = {"Default theme", "Theme 1", "Theme 2"};
    private final String[] animations = {"No animations yet"};
    private AppController appController;

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void initialize() {
        styleChoiceBox.getItems().addAll(styles);
        animationsChoiceBox.getItems().addAll(animations);

        styleChoiceBox.getSelectionModel().select(0);
        animationsChoiceBox.getSelectionModel().select(0);

        styleChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                appController.setStyleOnParts(newValue);
            }
        });
    }

    public void setTheme(String value) {
        anchorPane.getStylesheets().clear();
        anchorPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/components/header/title/titleCard" + value + ".css"))
                        .toExternalForm()
        );
    }

    public void backToMainScreenButtonListener() {
        appController.backToMainScreenClicked();
    }
}




