package DTO;

import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.util.HashMap;
import java.util.Map;

public class sheetDTO {
    private final int rowSize;
    private final int colSize;
    private final int rowHeight;
    private final int colWidth;
    private final Map<String, CellDataDTO> activeCells;
    private final String sheetName;
    private final int sheetVersionNumber;
    private final Map<Integer, sheetDTO> sheetMap;


    // Constructors, getters, setters
    public sheetDTO(SpreadSheetImpl sheet) {
        this.rowSize = sheet.getRowSize();
        this.colSize = sheet.getColumnSize();
        this.rowHeight = sheet.getRowHeight();
        this.colWidth = sheet.getColWidth();
        this.activeCells = convertCells(sheet);
        this.sheetName = sheet.getSheetName();
        this.sheetVersionNumber = sheet.getSheetVersionNumber();
        this.sheetMap = convertSheetMap(sheet);
    }

    private Map<String, CellDataDTO> convertCells(SpreadSheetImpl sheet) {
        Map<String, CellDataDTO> convertedCells = new HashMap<>();
        for (Map.Entry<String, CellImpl> entry : sheet.getSTLCells().entrySet()) {
            // Avoid recursion by checking if the cell's parent sheet is the current sheet
                convertedCells.put(entry.getKey(), new CellDataDTO(entry.getValue()));
        }
        return convertedCells;
    }

    // Helper method to convert sheet map
    private Map<Integer, sheetDTO> convertSheetMap(SpreadSheetImpl sheet) {
        Map<Integer, sheetDTO> convertedMap = new HashMap<>();
        for (Map.Entry<Integer, SpreadSheetImpl> entry : sheet.getSheetMap().entrySet()) {
            if (!entry.getValue().equals(sheet)) {
                sheetDTO sheetDTOInstance = new sheetDTO(entry.getValue());
                convertedMap.put(entry.getKey(), sheetDTOInstance);
            }
        }
        return convertedMap;
    }
    public int getRowSize() {
        return rowSize;
    }
    public int getColSize() {
        return colSize;
    }
    public int getRowHeight() {
        return rowHeight;
    }
    public int getColWidth() {
        return colWidth;
    }
    public Map<String, CellDataDTO> getActiveCells() {
        return activeCells;
    }
    public String getSheetName() {
        return sheetName;
    }
    public int getSheetVersionNumber() {
        return sheetVersionNumber;
    }
    public Map<Integer, sheetDTO> getSheetMap() {
        return sheetMap;
    }

}