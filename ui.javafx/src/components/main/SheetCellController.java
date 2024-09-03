package components.main;

import engine.impl.EngineImpl;

import javafx.stage.Stage;

public class SheetCellController {
    private Stage stage;
    private EngineImpl scene;


    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void setEngine(EngineImpl engine) {
        this.scene = engine;
    }
}
