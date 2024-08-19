package engineImpl;

import DTO.CellDataDTO;
import DTO.LoadDTO;
import DTO.exitDTO;
import DTO.sheetDTO;
import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.io.File;

public interface Engine {


    LoadDTO Load(File newFile);

    sheetDTO Display(SpreadSheetImpl sheet);

    CellDataDTO showCell(CellImpl cell);

    sheetDTO updateCell(SpreadSheetImpl sheet);

    sheetDTO showVersions(SpreadSheetImpl sheet);

    exitDTO exitSystem();

}
