package components.main;

import components.body.table.func.SheetFunctionalityController;
import components.body.table.view.GridSheetController;
import components.header.cellFunction.CellFunctionsController;
import components.header.loadFile.LoadFileController;
import components.header.title.TitleCardController;
import engine.impl.EngineImpl;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.File;

public class AppController {
    private Stage stage;
    private EngineImpl engine;

    @FXML ScrollPane titleCard;
    @FXML TitleCardController titleCardController;
    @FXML ScrollPane loadFile;
    @FXML
    LoadFileController loadFileController;
    @FXML ScrollPane cellFunctions;
    @FXML
    CellFunctionsController cellFunctionsController;
    @FXML ScrollPane gridSheet;
    @FXML
    GridSheetController gridSheetController;
    @FXML ScrollPane sheetFunctionality;
    @FXML
    SheetFunctionalityController sheetFunctionalityController;



    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void setEngine(EngineImpl engine) {
        this.engine = engine;
    }

    public EngineImpl getEngine() {
        return engine;
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
