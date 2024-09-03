package components.main;

import engine.impl.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = getClass().getResource("app.fxml");
        loader.setLocation(mainFXML);
        BorderPane root = loader.load();

        AppController controller = loader.getController();
        EngineImpl engine = new EngineImpl();
        controller.setPrimaryStage(primaryStage);
        controller.setEngine(engine);

        primaryStage.setTitle("Sheet Cell task 2");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}