package sheet.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpreadSheetImpl {
    private final int rowSize;
    private final int columnSize;
    private Map<String, CellImpl> activeCells;
    private final String sheetName;
    private int sheetVersionNumber;
    private List<SpreadSheetImpl> sheetList;

    public SpreadSheetImpl(int rowSize, int columnSize, String sheetName) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.activeCells = new HashMap<>();
        this.sheetName = sheetName;
        this.sheetVersionNumber = 1;
        sheetList = new ArrayList<>();
        sheetList.addLast(this); //the first sheet in the array

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

    public CellImpl getCell(String cellId) {
//        if (!cellId.matches("^[A-Za-z]\\d+$")) {
//            throw new IllegalArgumentException("Input must be in the format of a letter followed by one or more digits. Found: " + cellId);
//        }
//        char letter = cellId.charAt(0); //taking the char
//        int col = Character.getNumericValue(letter) - Character.getNumericValue('A'); //getting the col
//        int row = Integer.parseInt(cellId.substring(1))-1; //1=> after the letter.
//        if (col < 0 || row < 0 || row >= rowSize || col >= columnSize) {
//            throw new IllegalArgumentException("The specified column or row number is invalid. Found: " + cellId + " please make sure that the CellImpl you refer to exists.");
//        }
////        if (!sheet[row][col].getId().equals(cellId)) {
////            throw new IllegalArgumentException("Something went wrong, the Id you refer to do not match the specified column or row. Try again.");
////        }
         return null;
    }


}

