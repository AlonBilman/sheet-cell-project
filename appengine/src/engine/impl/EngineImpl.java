package engine.impl;

import dto.*;
import checkfile.STLSheet;
import engine.api.Engine;
import sheet.impl.CellImpl;
import sheet.impl.Range;
import sheet.impl.SpreadSheetImpl;

import java.io.*;
import java.util.*;

public class EngineImpl implements Engine, Serializable {
    private static final int MAX_ROWS = 50;
    private static final int MIN_ROWS_AND_COLS = 1;
    private static final int MAX_COLS = 20;
    private SpreadSheetImpl spreadSheet;
    Map<Integer, sheetDTO> sheets = new HashMap<>();

    public EngineImpl() {
        this.spreadSheet = null;
    }

    public void initSheet(STLSheet stlSheet) {
        if (stlSheet == null) {
            throw new NullPointerException("STLSheet is null");
        }
        if (stlSheet.getSTLLayout().getRows() > MAX_ROWS) {
            throw new IllegalArgumentException("XML file inserted more than 50 rows");
        }
        if (stlSheet.getSTLLayout().getRows() < MIN_ROWS_AND_COLS) {
            throw new IllegalArgumentException("XML file inserted less than " + MIN_ROWS_AND_COLS + " rows");
        }
        if (stlSheet.getSTLLayout().getColumns() > MAX_COLS) {
            throw new IllegalArgumentException("XML file inserted contains more than 20 columns");
        }
        if (stlSheet.getSTLLayout().getColumns() < MIN_ROWS_AND_COLS) {
            throw new IllegalArgumentException("XML file inserted less than " + MIN_ROWS_AND_COLS + " columns");
        }
        this.spreadSheet = new SpreadSheetImpl(stlSheet);
        sheets.clear();
        sheets.put(this.spreadSheet.getSheetVersionNumber(), new sheetDTO(this.spreadSheet));
    }

    @Override
    public sheetDTO Display() {
        return new sheetDTO(this.spreadSheet);
    }

    @Override
    public CellDataDTO showCell(String id) {
        return new CellDataDTO(this.spreadSheet.getCellOrCreateIt(id));
    }

    @Override
    public sheetDTO updateCell(String cellId, String value) {
        if (value != null && value.matches(".*\\S.*"))
            value = value.trim();
        try {
            this.spreadSheet.changeCell(cellId, value);
            sheets.put(this.spreadSheet.getSheetVersionNumber(), new sheetDTO(this.spreadSheet));
        } catch (Exception e) {
            this.spreadSheet = this.spreadSheet.getSheetBeforeChange(); //1 snapshot back
            throw e;
        }
        return new sheetDTO(this.spreadSheet);
    }

    public void savePositionToFile(String folderPath, String fileName) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, fileName.trim() + ".ser"); //trim in order to remove spaces
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }

    public static EngineImpl resumePositionToEngine(String filePath, String fileName) throws IOException, ClassNotFoundException {
        File file = new File(filePath + "\\" + fileName.trim() + ".ser");
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (EngineImpl) ois.readObject();
        }
    }

    //-------------------------------------------------------------------------------------
    public void addRange(String name, String params) {
        if (params == null || !params.contains("..")) {
            throw new IllegalArgumentException("Invalid range format. Expected format: <Letter,Number>..<Letter,Number>");
        }

        String[] cellIdentifiers = params.split("\\.\\.");
        if (cellIdentifiers.length != 2) {
            throw new IllegalArgumentException("Invalid range format. Expected format: <Letter,Number>..<Letter,Number>");
        }

        String topLeftCellId = cellIdentifiers[0];
        String bottomRightCellId = cellIdentifiers[1];
        try {
            spreadSheet.addRange(name, topLeftCellId, bottomRightCellId);
        } catch (Exception e) {
            spreadSheet = this.spreadSheet.getSheetBeforeChange();
            throw e;
        }
    }

    public RangeDTO getRangeDto(String id) {
      sheetDTO sheet = this.Display();
      return sheet.getRange(id);
    }

    //-------------------------------------------------------------------------------------

    @Override
    public LoadDTO Load(File newFile) {
        return new LoadDTO(newFile);
    }

    public boolean containSheet() {
        return this.spreadSheet != null;
    }

    public Map<Integer, sheetDTO> getSheets() {
        return sheets;
    }

    public sheetDTO getSheet(int version) {
        return sheets.get(version);
    }

    @Override
    public exitDTO exitSystem() {
        return new dto.exitDTO();
    }
}
