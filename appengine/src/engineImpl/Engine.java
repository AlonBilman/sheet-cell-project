package engineImpl;

import DTO.CellDataDTO;
import DTO.LoadDTO;
import DTO.exitDTO;
import DTO.sheetDTO;
import sheet.impl.CellImpl;


import java.io.File;

public interface Engine {


    LoadDTO Load(File newFile);

    sheetDTO Display();

    CellDataDTO showCell(String id);

    sheetDTO updateCell(String cellId, String value);

    sheetDTO showVersions();

    exitDTO exitSystem();



}
