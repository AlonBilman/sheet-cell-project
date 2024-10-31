package http.dto;

import java.util.Map;

public class SheetDtoResp {
    private int rowSize;
    private int colSize;
    private int rowHeight;
    private int colWidth;
    private Map<String, CellDataDtoResp> activeCells;
    private Map<String, RangeDtoResp> activeRanges;
    private String sheetName;
    private int sheetVersionNumber;

    public RangeDtoResp getRange(String name) {
        RangeDtoResp range = activeRanges.get(name);
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

    public Map<String, CellDataDtoResp> getActiveCells() {
        return activeCells;
    }

    public Map<String, RangeDtoResp> getActiveRanges() {
        return activeRanges;
    }

    public String getSheetName() {
        return sheetName;
    }

    public int getSheetVersionNumber() {
        return sheetVersionNumber;
    }

}
