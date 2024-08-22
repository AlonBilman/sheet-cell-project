package engine.impl;

import DTO.CellDataDTO;
import DTO.LoadDTO;
import DTO.exitDTO;
import DTO.sheetDTO;
import FileCheck.STLSheet;
import engine.api.Engine;
import sheet.impl.SpreadSheetImpl;

import java.io.File;

public class EngineImpl implements Engine {

    private SpreadSheetImpl spreadSheet;

    public EngineImpl() {
        this.spreadSheet = null;
    }

    public void initSheet(STLSheet stlSheet) {
        if (stlSheet == null) {
            throw new NullPointerException("STLSheet is null");
        }
        this.spreadSheet = new SpreadSheetImpl(stlSheet);
    }

    @Override
    public sheetDTO Display() {
        return new sheetDTO(this.spreadSheet);
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

    public boolean containSheet() {
        return this.spreadSheet != null;
    }

    public int getVersionNumber() {
        return this.spreadSheet.getSheetVersionNumber();
    }

    @Override
    public exitDTO exitSystem() {
        return new DTO.exitDTO();
    }
}
