package engineImpl;

import DTO.CellDataDTO;
import DTO.LoadDTO;
import DTO.exitDTO;
import DTO.sheetDTO;
import sheet.impl.SpreadSheetImpl;

import java.io.File;

public class EngineImpl implements Engine {

    private SpreadSheetImpl spreadSheet;

    public SpreadSheetImpl getSpreadSheet() {
        return spreadSheet;
    }

   public EngineImpl(SpreadSheetImpl spreadSheet) {
        this.spreadSheet = spreadSheet;
    }

    public void setSheet(SpreadSheetImpl sheet){
       this.spreadSheet = sheet;
    }

    @Override
    public sheetDTO Display() {
            return new sheetDTO(spreadSheet);
    }

    @Override
    public CellDataDTO showCell(String id) {
       return new CellDataDTO(this.spreadSheet.getCell(id));
        }

    @Override
    public sheetDTO updateCell(String cellId, String value) {
        try {
            this.spreadSheet.changeCell(cellId, value);
        } catch (Exception e) {
            this.spreadSheet = this.spreadSheet.getSheetBeforeChange(); //1 snapshot back
            throw e;
        }
        return new sheetDTO(this.spreadSheet);
    }

    @Override
    public sheetDTO showVersions() {
        return new sheetDTO(this.spreadSheet);
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
