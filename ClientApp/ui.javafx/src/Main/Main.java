package Main;

import components.page.view.loginscreen.LoginController;
import components.page.view.mainscreen.MainScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

import static http.utils.HttpClientUtil.shutdown;

public class Main extends Application {

    private Stage primaryStage;
    private MainScreenController mainScreenController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(event -> {
            confirmExit();
        });
        try {
            showLoginPage();
        }
        catch (Exception e) {
            System.out.println("Problem with loading page: " + e.getMessage());
        }
    }

    private void showLoginPage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL loginFXML = getClass().getResource("/components/page/view/loginscreen/loginPage.fxml");
        loader.setLocation(loginFXML);
        Parent root = loader.load();

        Scene scene = new Scene(root, 510, 350);
        primaryStage.setTitle("Sheet Cell - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
        LoginController loginController = loader.getController();
        loginController.setLoginListener(() -> {
            showMainApp(loginController.getUserName());
        });
    }

    private void confirmExit() {
        if (mainScreenController != null) {
            mainScreenController.stopListRefresher();
            mainScreenController.stopChatRefresher();
        }
        primaryStage.close();
        shutdown();
    }

    private void showMainApp(String userName) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL mainFXML = getClass().getResource("/components/page/view/mainscreen/mainScreen.fxml");
            loader.setLocation(mainFXML);
            Parent root = loader.load();
            Scene scene = new Scene(root, 915, 710);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sheet Cell - Main Screen");
            primaryStage.centerOnScreen();
            mainScreenController = loader.getController();
            mainScreenController.setStage(primaryStage);
            mainScreenController.setUserName(userName);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
