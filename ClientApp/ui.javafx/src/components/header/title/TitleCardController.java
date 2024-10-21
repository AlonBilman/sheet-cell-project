package components.header.title;

import components.page.view.sheetscreen.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class TitleCardController {

    @FXML
    public Text sheetCellText;
    @FXML
    public Button backToMainScreenButton;
    @FXML
    public Label nameLabel;
    @FXML
    private AnchorPane anchorPane;

    private AppController appController;

    public void setMainController(AppController mainController) {
        this.appController = mainController;
    }

    public void setName(String name){
        nameLabel.setText(name);
    }

    public void backToMainScreenButtonListener() {
        appController.backToMainScreenClicked();
    }
}




