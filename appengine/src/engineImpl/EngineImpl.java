package engineImpl;

import DTO.CellDataDTO;
import DTO.LoadDTO;

import DTO.sheetDTO;
import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.io.File;

public class EngineImpl implements Engine {

    @Override
    public sheetDTO Display(SpreadSheetImpl sheet) {
            return new sheetDTO(sheet);
    }

    @Override
    public CellDataDTO showCell(CellImpl cell) {
        return new CellDataDTO(cell);
    }

    @Override
    public DTO.sheetDTO updateCell(SpreadSheetImpl sheet) {
        return null;
    }

    @Override
    public sheetDTO showVersions(SpreadSheetImpl sheet) {
        return new sheetDTO(sheet);
    }

    @Override
    public LoadDTO Load(File newFile) {
        return new LoadDTO(newFile);
    }

    @Override
    public DTO.exitDTO exitSystem() {
        return new DTO.exitDTO();
    }
}
