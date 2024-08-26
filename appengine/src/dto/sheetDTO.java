package dto;

import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class sheetDTO implements Serializable {
    private final int rowSize;
    private final int colSize;
    private final int rowHeight;
    private final int colWidth;
    private final Map<String, CellDataDTO> activeCells;
    private final String sheetName;
    private final int sheetVersionNumber;

    // Constructors, getters, setters
    public sheetDTO(SpreadSheetImpl sheet) {
        this.rowSize = sheet.getRowSize();
        this.colSize = sheet.getColumnSize();
        this.rowHeight = sheet.getRowHeight();
        this.colWidth = sheet.getColWidth();
        this.activeCells = convertCells(sheet);
        this.sheetName = sheet.getSheetName();
        this.sheetVersionNumber = sheet.getSheetVersionNumber();

    }

    private Map<String, CellDataDTO> convertCells(SpreadSheetImpl sheet) {
        Map<String, CellDataDTO> convertedCells = new HashMap<>();
        for (Map.Entry<String, CellImpl> entry : sheet.getSTLCells().entrySet()) {
            convertedCells.put(entry.getKey(), new CellDataDTO(entry.getValue()));
        }
        return convertedCells;
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

}
