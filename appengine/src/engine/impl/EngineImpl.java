package engine.impl;

import dto.*;
import checkfile.STLSheet;
import engine.api.Engine;
import expression.api.ObjType;
import sheet.impl.CellImpl;
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

    private Map<String, List<CellDataDTO>> rowMapBuilder(String params) {
        Map<String, List<CellDataDTO>> rowMap = new HashMap<>();
        String[] cellIdentifiers = checkRangeParams(params);
        Set<CellImpl> setOfCells = this.spreadSheet.getSetOfCellsForRange(cellIdentifiers[0], cellIdentifiers[1]);

        List<CellDataDTO> setOfCellsDto = new ArrayList<>();
        for (CellImpl cell : setOfCells) {
            setOfCellsDto.add(showCell(cell.getId()));
        }
        for (CellDataDTO cell : setOfCellsDto) {
            String rowKey = String.valueOf(cell.getRow());
            rowMap.computeIfAbsent(rowKey, k -> new ArrayList<>()).add(cell);
        }
        return rowMap;
    }


    public Map<String, List<CellDataDTO>> sort(String params, List<String> sortBy) {
        Map<String, List<CellDataDTO>> rowMap = rowMapBuilder(params);

        List<Map.Entry<String, List<CellDataDTO>>> rowEntries = new ArrayList<>(rowMap.entrySet());

        rowEntries.sort((entry1, entry2) -> {
            List<CellDataDTO> row1Cells = entry1.getValue();
            List<CellDataDTO> row2Cells = entry2.getValue();

            for (String column : sortBy) {
                Double value1 = getColumnValue(row1Cells, column);
                Double value2 = getColumnValue(row2Cells, column);
                int comparison = compareValues(value1, value2);
                if (comparison != 0) {
                    return comparison; //if not equal, return the comparison result
                }
            }
            return 0; //if everything is equal keep the same, it keeps the same with strings
        });

        //until here rowEntries sorted as it should. meaning the first list would be the first row,
        //the second list would be the second and so on.
        return null;
    }



    // Helper method to get the value of the cell in the specified column
    private Double getColumnValue(List<CellDataDTO> cells, String column) {
        // Find the cell in the row that matches the column
        for (CellDataDTO cell : cells) {
            if (cell.getCol().equals(column) && cell.getEffectiveValue().getObjType().equals(ObjType.NUMERIC)) {
                return (Double)cell.getEffectiveValue().getValue();
            }
        }
        return null; 
    }

    // Helper method to compare values (numeric or string comparison)
    private int compareValues(Double value1, Double value2) {
        if(value2 == null && value1 == null) {
            return 0;
        }
        else if (value2 == null) {
            return 1;
        }
        else if (value1 == null) {
            return -1;
        }
        return Double.compare(value1, value2);
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

    private String[] checkRangeParams(String params) {
        if (params == null || !params.contains("..")) {
            throw new IllegalArgumentException("Invalid range format. Expected format: <Letter,Number>..<Letter,Number>");
        }
        String[] cellIdentifiers = params.split("\\.\\.");
        if (cellIdentifiers.length != 2) {
            throw new IllegalArgumentException("Invalid range format. Expected format: <Letter,Number>..<Letter,Number>");
        }
        return cellIdentifiers;
    }

    public void addRange(String name, String params) {
        String[] cellIdentifiers = checkRangeParams(params); //may result exception
        String topLeftCellId = cellIdentifiers[0];
        String bottomRightCellId = cellIdentifiers[1];
        spreadSheet.addRange(name, topLeftCellId, bottomRightCellId); //may result exception
        //there is no need to revert to the last spreadsheet
    }

    public RangeDTO getRangeDto(String id) {
        sheetDTO sheet = this.Display();
        return sheet.getRange(id);
    }

    public void deleteRange(String name) {
        this.spreadSheet.deleteRange(name);
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
