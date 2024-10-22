package components.header.title;

import components.page.view.sheetscreen.AppController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TitleCardController {

    @FXML
    public Text sheetCellText;
    @FXML
    public Button backToMainScreenButton;
    @FXML
    public Label nameLabel;
    @FXML
    public Button reloadSheetButton;
    @FXML
    private AnchorPane anchorPane;

    private AppController appController;
    private boolean isBreathing = false;

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void setName(String name) {
        nameLabel.setText(name);
    }

    public void backToMainScreenButtonListener() {
        appController.backToMainScreenClicked();
    }

    public void reloadSheetListener() {
        appController.updateSheetDtoVersion();
        toggleBreathing();
    }

    private void toggleBreathing() {
        if (isBreathing) {
            reloadSheetButton.getStyleClass().remove("glow-effect");
            reloadSheetButton.setOpacity(1);
            reloadSheetButton.setStyle("");
        } else {
            reloadSheetButton.getStyleClass().add("glow-effect");

            Timeline breatheTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> reloadSheetButton.setOpacity(1)),
                    new KeyFrame(Duration.seconds(1.0), e -> reloadSheetButton.setOpacity(0.85)),
                    new KeyFrame(Duration.seconds(2.0), e -> reloadSheetButton.setOpacity(1))
            );

            breatheTimeline.setCycleCount(Animation.INDEFINITE);
            breatheTimeline.play();
        }
        isBreathing = !isBreathing;
    }
}
