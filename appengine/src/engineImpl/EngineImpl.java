package engineImpl;

import DTO.CellDataDTO;
import DTO.LoadDTO;
import DTO.exitDTO;
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
            if (cell == null) {
                throw new IllegalArgumentException("Cannot create CellDataDTO from a null cell.");
            }
            return new CellDataDTO(cell);
        }

    @Override
    public sheetDTO updateCell(SpreadSheetImpl sheet, String cellId, String value) {
        try{
            sheet.changeCell(cellId, value);
        }catch (Exception e){
            sheet = sheet.getSheetBeforeChange();
            throw  e ;
        }
        return new sheetDTO(sheet);
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
    public exitDTO exitSystem() {
        return new DTO.exitDTO();
    }
}
