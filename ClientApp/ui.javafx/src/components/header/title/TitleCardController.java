package components.header.title;

import components.page.view.sheetscreen.AppController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;
import java.util.Timer;

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
    private Timer versionTimer;
    private VersionRefresher versionRefresher;
    private BooleanProperty autoUpdate = new SimpleBooleanProperty(true);
    private int currVersion;
    private Timeline breatheTimeline;

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
        if (isBreathing) {
            toggleBreathing(); //stop breathing if active
        }
        reloadSheetButton.setDisable(true);
    }

    private void toggleBreathing() {
        if (isBreathing) {
            stopBreathingAnimation();
        } else {
            startBreathingAnimation();
        }
        isBreathing = !isBreathing;
    }

    private void startBreathingAnimation() {
        if (breatheTimeline == null) {
            breatheTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> reloadSheetButton.setOpacity(1)),
                    new KeyFrame(Duration.seconds(1.0), e -> reloadSheetButton.setOpacity(0.7)),
                    new KeyFrame(Duration.seconds(2.0), e -> reloadSheetButton.setOpacity(1))
            );
            breatheTimeline.setCycleCount(Animation.INDEFINITE);
        }

        if (breatheTimeline.getStatus() != Animation.Status.RUNNING) {
            reloadSheetButton.getStyleClass().add("glow-effect");
            breatheTimeline.play();
        }
    }

    private void stopBreathingAnimation() {
        if (breatheTimeline != null && breatheTimeline.getStatus() == Animation.Status.RUNNING) {
            breatheTimeline.stop();
            reloadSheetButton.getStyleClass().remove("glow-effect");
            reloadSheetButton.setOpacity(1);
        }
    }

    public void startVersionRefresher(Map<String, String> queryParams, int currVersion) {
        stopVersionRefresher();
        this.currVersion = currVersion;
        versionRefresher = new VersionRefresher(autoUpdate, this::onResponseRefresher, queryParams);
        versionTimer = new Timer();
        versionTimer.schedule(versionRefresher, 0, 3000);
    }

    public void stopVersionRefresher() {
        if (versionTimer != null) {
            versionTimer.cancel();
            versionTimer = null;
        }
        if (versionRefresher != null) {
            versionRefresher.cancel();
            versionRefresher = null;
        }
    }

    private void onResponseRefresher(int newVersion) {
        if (newVersion > this.currVersion) {
            System.out.println("DETECTED new version : "+newVersion);
            stopVersionRefresher();
            reloadSheetButton.setDisable(false);
            toggleBreathing();
        }
    }
}
