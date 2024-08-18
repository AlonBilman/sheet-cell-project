package sheet.impl;

import FileCheck.STLCell;
import FileCheck.STLCells;
import FileCheck.STLSheet;
import expression.api.Expression;
import expression.api.ObjType;
import sheet.api.EffectiveValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpreadSheetImpl {
    private final int rowSize;
    private final int columnSize;
    private final int colWidth;
    private final int rowHeight;
    private Map<String, CellImpl> activeCells;
    private final String sheetName;
    private int sheetVersionNumber;
    private Map<Integer, SpreadSheetImpl> sheetMap;

    public SpreadSheetImpl(String sheetName, int rowSize, int columnSize, int colWidth, int rowHeight) {
        this.sheetName = sheetName;
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.colWidth = colWidth;
        this.rowHeight = rowHeight;
        this.sheetVersionNumber = 1;
        this.activeCells = new HashMap<>();
        this.sheetMap = new HashMap<>();

    }

    public SpreadSheetImpl(STLSheet stlSheet) {
        this.rowSize = stlSheet.getSTLLayout().getRows();
        this.columnSize = stlSheet.getSTLLayout().getColumns();
        this.activeCells = new HashMap<>();
        STLCells stlCells = stlSheet.getSTLCells();
        if(stlCells != null) {
            addCells(stlCells.getSTLCell(),this);
        }
        this.sheetName = stlSheet.getName();
        this.sheetVersionNumber = 1;
        this.sheetMap = new HashMap<>();
        this.sheetMap.put(this.sheetVersionNumber, this);
 	    this.colWidth = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();
        this.rowHeight = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
    }

    public void addCells( List<STLCell> cells,SpreadSheetImpl sheet) {
        if(cells == null || cells.isEmpty()) {
            return;
        }
        for (STLCell cell : cells) {
            CellImpl cellImpl = new CellImpl(cell,sheet);
            this.activeCells.put(cellImpl.getId() , cellImpl);
        }
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getColWidth() {
        return colWidth;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void addCell(String id, CellImpl cell) {
        activeCells.put(id, cell);
    }


    public void setSheetMap(Map<Integer, SpreadSheetImpl> sheetMap) {
        this.sheetMap = sheetMap;
    }

    public void changeCell(String id,String newOriginalVal) {
        CellImpl cell = activeCells.get(id);
        cell.setOriginalValue(newOriginalVal,this);
    }

    public Map<Integer, SpreadSheetImpl> getSheetMap() {
        return sheetMap;
    }


    public String getSheetName() {
        return sheetName;
    }


    public int getSheetVersionNumber() {
        return sheetVersionNumber;
    }


    public void setSheetVersionNumber(int sheetVersionNumber) {
        this.sheetVersionNumber = sheetVersionNumber;
    }


    public void addNewVersion(STLSheet newSheet) {
        SpreadSheetImpl newSpreadSheet = new SpreadSheetImpl(newSheet);
        this.sheetVersionNumber++;
        this.sheetMap.put(this.sheetVersionNumber, newSpreadSheet);
    }

    public EffectiveValue ref(EffectiveValue id, String IdThatCalledMe) {
        String theCellThatRefIsReferingTo = (String) id.getValue();
        CellImpl curr = getCell((String) id.getValue());
        if (curr == null)
            throw new RuntimeException("No such cell, create it before referring to it.");
        curr.addAffectsOnId(IdThatCalledMe);
        return curr.getEffectiveValue(); //returns EffectiveValue
    }


    public CellImpl getCell(String cellId) {
        if (!cellId.matches("^[A-Za-z]\\d+$")) {
            throw new IllegalArgumentException("Input must be in the format of a letter followed by one or more digits. Found: "+ cellId);
        }
        char letter = cellId.charAt(0); //taking the char
        int col = Character.getNumericValue(letter) - Character.getNumericValue('A'); //getting the col

        int row = Integer.parseInt(cellId.substring(1)) - 1; //1=> after the letter.
        if (col < 0 || row < 0 || row >= rowSize || col >= columnSize) {
            throw new IllegalArgumentException("The specified column or row number is invalid. Found: " + cellId + " please make sure that the CellImpl you refer to exists.");
        }
        CellImpl cell = activeCells.get(cellId);
        if (cell == null) {
            throw new RuntimeException("No such cell, it contains nothing, before referring to it you should update it's content.");
        }
        return cell;
    }

}



