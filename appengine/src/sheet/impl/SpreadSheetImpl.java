package sheet.impl;

import FileCheck.STLSheet;

import java.util.HashMap;
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
    private SpreadSheetImpl sheet;

    public SpreadSheetImpl(STLSheet stlSheet) {
        this.rowSize = stlSheet.getSTLLayout().getRows();
        this.columnSize = stlSheet.getSTLLayout().getColumns();
        this.activeCells = new HashMap<>();
        this.sheetName = stlSheet.getName();
        this.sheetVersionNumber = 1;
        this.sheetMap = new HashMap<>();
        this.sheetMap.put(this.sheetVersionNumber, this);
        this.colWidth = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();
        this.rowHeight = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();

    }


    public void setSheetMap(Map<Integer, SpreadSheetImpl> sheetMap) {
        this.sheetMap = sheetMap;
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


    public CellImpl getCell(String cellId) {
        if (!cellId.matches("^[A-Za-z]\\d+$")) {
            throw new IllegalArgumentException("Input must be in the format of a letter followed by one or more digits. Found: " + cellId);
        }
        char letter = cellId.charAt(0); //taking the char
        int col = Character.getNumericValue(letter) - Character.getNumericValue('A'); //getting the col
        int row = Integer.parseInt(cellId.substring(1)) - 1; //1=> after the letter.
        if (col < 0 || row < 0 || row >= rowSize || col >= columnSize) {
            throw new IllegalArgumentException("The specified column or row number is invalid. Found: " + cellId + " please make sure that the CellImpl you refer to exists.");
        }
//      if (!sheet[row][col].getId().equals(cellId)) {
//            throw new IllegalArgumentException("Something went wrong, the Id you refer to do not match the specified column or row. Try again.");
//       }
        return null;
    }

}


