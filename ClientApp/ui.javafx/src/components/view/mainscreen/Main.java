package components.view.mainscreen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../mainscreen/mainscreen.fxml"));
            Parent root = loader.load();

            // Set the scene
            Scene scene = new Scene(root);
            primaryStage.setTitle("Sheet Cell Main Screen");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
