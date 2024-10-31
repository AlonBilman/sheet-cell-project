package dto;

import sheet.impl.CellImpl;
import sheet.impl.Range;
import sheet.impl.SpreadSheetImpl;

import java.io.Serializable;
import java.util.*;

public class SheetDto implements Serializable {
    private final int rowSize;
    private final int colSize;
    private final int rowHeight;
    private final int colWidth;
    private final Map<String, CellDataDto> activeCells;
    private final Map<String, RangeDto> activeRanges;
    private final String sheetName;
    private final int sheetVersionNumber;

    public SheetDto(SpreadSheetImpl sheet) {
        this.rowSize = sheet.getRowSize();
        this.colSize = sheet.getColumnSize();
        this.rowHeight = sheet.getRowHeight();
        this.colWidth = sheet.getColWidth();
        this.activeCells = convertCells(sheet);
        this.activeRanges = convertRanges(sheet);
        this.sheetName = sheet.getSheetName();
        this.sheetVersionNumber = sheet.getSheetVersionNumber();
    }

    private Map<String, CellDataDto> convertCells(SpreadSheetImpl sheet) {
        Map<String, CellDataDto> convertedCells = new HashMap<>();
        for (Map.Entry<String, CellImpl> entry : sheet.getActiveCells().entrySet()) {
            convertedCells.put(entry.getKey(), new CellDataDto(entry.getValue()));
        }
        return convertedCells;
    }

    private Map<String, RangeDto> convertRanges(SpreadSheetImpl sheet) {
        Map<String, RangeDto> convertedRanges = new HashMap<>();
        for (Map.Entry<String, Range> entry : sheet.getActiveRanges().entrySet()) {
            //when creating a RangeDto I convert the cells in them to DTOs too.
            // Look at the constructor of RangeDto for more details
            convertedRanges.put(entry.getKey(), new RangeDto(entry.getValue()));
        }
        return convertedRanges;
    }

    public RangeDto getRange(String name) {
        RangeDto range = activeRanges.get(name);
        if (range == null) {
            throw new IllegalArgumentException("No such range: " + name);
        }
        return range;
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

    public Map<String, CellDataDto> getActiveCells() {
        return activeCells;
    }

    public Map<String, RangeDto> getActiveRanges() {
        return activeRanges;
    }

    public String getSheetName() {
        return sheetName;
    }

    public int getSheetVersionNumber() {
        return sheetVersionNumber;
    }

}
