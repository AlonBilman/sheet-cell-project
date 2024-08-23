package engine.api;

import dto.CellDataDTO;
import dto.LoadDTO;
import dto.exitDTO;
import dto.sheetDTO;


import java.io.File;

public interface Engine {

    LoadDTO Load(File newFile);

    sheetDTO Display();

    CellDataDTO showCell(String id);

    sheetDTO updateCell(String cellId, String value);

    exitDTO exitSystem();

}
