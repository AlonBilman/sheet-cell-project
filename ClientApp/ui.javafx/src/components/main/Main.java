package components.main;

import components.login.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

import static http.HttpClientUtil.shutdown;

public class Main extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showLoginPage();
    }

    // Method to show the login page
    private void showLoginPage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL loginFXML = getClass().getResource("../login/loginPage.fxml"); // Update the path accordingly
        loader.setLocation(loginFXML);
        Parent root = loader.load();

        // Set up the login scene
        Scene scene = new Scene(root, 500, 350);
        primaryStage.setTitle("Sheet Cell - Login");
        primaryStage.setScene(scene);

        // Set close request handler for the primary stage
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Consume the event to prevent default closing behavior
            confirmExit(); // Call the exit confirmation method
        });

        // Show the login stage
        primaryStage.show();

        // Logic to handle successful login
        LoginController loginController = loader.getController();
        loginController.setLoginListener(this::showMainApp); // Pass the method reference
    }

    private void confirmExit() {
        primaryStage.close();
        shutdown();
    }

    // Method to show the main application
    private void showMainApp() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL mainFXML = getClass().getResource("app.fxml"); // Update the path accordingly
            loader.setLocation(mainFXML);
            Parent root = loader.load();
            Scene scene = new Scene(root, 1120, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sheet Cell - Application");

            // Center the main application on the screen
            primaryStage.centerOnScreen(); // Center the stage on the screen

            primaryStage.show(); // Ensure the stage is shown
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }
}
