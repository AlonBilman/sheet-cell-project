package engineImpl;

import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.io.File;

public interface Engine {

    EngineImpl.loadDTO Load(File newFile, File oldFile);

    public EngineImpl.loadDTO Load();

    public EngineImpl.sheetDTO Display(SpreadSheetImpl sheet);

    public EngineImpl.CellDataDTO showCell(CellImpl cell);

    public EngineImpl.sheetDTO updateCell(SpreadSheetImpl sheet);

    public EngineImpl.sheetDTO showVersions(SpreadSheetImpl sheet);

    public EngineImpl.exitDTO exitSystem();

}
