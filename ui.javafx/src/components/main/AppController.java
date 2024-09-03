package components.main;

import engine.impl.EngineImpl;
import javafx.stage.Stage;

import java.io.File;

public class AppController {
    private Stage stage;
    private EngineImpl engine;


    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void setEngine(EngineImpl engine) {
        this.engine = engine;
    }

    public boolean checkFile(File file) {
        try {
            engine.Load(file);
            return true;
        }
        catch (Exception e) {
            return false;
        }

    }
}
