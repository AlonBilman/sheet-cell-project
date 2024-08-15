package sheet.impl;
import java.util.ArrayList;
import java.util.List;

public class SpreadSheet {
    private final int rowSize;
    private final int columnSize;
    private Cell[][] sheet;
    private final String sheetName;
    private int sheetVersionNumber;
    private List<SpreadSheet> sheetList;

    public SpreadSheet(int rowSize, int columnSize, String sheetName) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.sheet = new Cell[rowSize][columnSize];
        buildSheet();
        this.sheetName = sheetName;
        this.sheetVersionNumber = 1;
        sheetList = new ArrayList<>();
        sheetList.addLast(this); //the first sheet in the array

    }

    private void buildSheet() {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                this.sheet[i][j] = new Cell(i, j);
            }
        }
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

    public Cell getCell(String cellId) {
        if (!cellId.matches("^[A-Za-z]\\d+$")) {
            throw new IllegalArgumentException("Input must be in the format of a letter followed by one or more digits. Found: " + cellId);
        }
        char letter = cellId.charAt(0); //taking the char
        int col = Character.getNumericValue(letter) - Character.getNumericValue('A'); //getting the col
        int row = Integer.parseInt(cellId.substring(1))-1; //1=> after the letter.
        if (col < 0 || row < 0 || row >= rowSize || col >= columnSize) {
            throw new IllegalArgumentException("The specified column or row number is invalid. Found: " + cellId + " please make sure that the Cell you refer to exists.");
        }
        if (!sheet[row][col].getId().equals(cellId)) {
            throw new IllegalArgumentException("Something went wrong, the Id you refer to do not match the specified column or row. Try again.");
        }
        return this.sheet[row][col];
    }


}

