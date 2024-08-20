package sheet.impl;

import FileCheck.*;
import sheet.api.EffectiveValue;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpreadSheetImpl implements Serializable {
    private final int rowSize;
    private final int columnSize;
    private final int colWidth;
    private final int rowHeight;
    private Map<String, CellImpl> activeCells;
    private final String sheetName;
    private int sheetVersionNumber;
    private Map<Integer, SpreadSheetImpl> sheetMap;
    private SpreadSheetImpl sheetBeforeChange=null;

    public SpreadSheetImpl(String sheetName, int rowSize, int columnSize, int colWidth, int rowHeight) {
        this.sheetName = sheetName;
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.colWidth = colWidth;
        this.rowHeight = rowHeight;
        this.sheetVersionNumber = 1;
        this.activeCells = new HashMap<>();
        this.sheetMap = new HashMap<>();
        CellImpl.setSpreadSheet(this);
        sheetBeforeChange=deepCopy();
    }

public SpreadSheetImpl deepCopy() {
    try {
        // Serialize the object to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(this);
        objectOutputStream.flush();
        objectOutputStream.close();

        // Deserialize the byte array to a new object
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (SpreadSheetImpl) objectInputStream.readObject();

    } catch (NotSerializableException e) {
        System.err.println("A field is not serializable: " + e.getMessage());
        throw new RuntimeException("Failed to deep copy SpreadSheetImpl due to non-serializable field", e);
    } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("Failed to deep copy SpreadSheetImpl", e);
    }
}

    public SpreadSheetImpl(STLSheet stlSheet) {
        CellImpl.setSpreadSheet(this);
        this.activeCells = new HashMap<>();
        this.sheetVersionNumber = 1;
        this.rowSize = stlSheet.getSTLLayout().getRows();
        this.columnSize = stlSheet.getSTLLayout().getColumns();
        this.colWidth = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();
        this.rowHeight = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        this.sheetName = stlSheet.getName();
        this.sheetMap = new HashMap<>();
        this.sheetMap.put(this.sheetVersionNumber, this); // Need to be fixed
        STLCells stlCells = stlSheet.getSTLCells();
        if(stlCells != null) {
            addCells(stlCells.getSTLCell());
        }
        sheetBeforeChange=deepCopy();
    }

    public void changeCell(String id, String newOriginalVal) {
            sheetBeforeChange=deepCopy();
            CellImpl.setSpreadSheet(this);
            checkCellId(id);
            CellImpl cell = activeCells.get(id);
            if(cell==null) //meaning there is no cell like this activated
            {
                checkCellId(id);
                addCell(id.charAt(1)- '0', id.substring(0,1), newOriginalVal);
            }
            else {
                cell.setOriginalValue(newOriginalVal);
                updateVersionNumber();
            }

    }

    public void addCell(int row, String col, String newOriginalVal){
        CellImpl.setSpreadSheet(this);
        sheetBeforeChange=deepCopy();
        CellImpl cell = new CellImpl(row,col,newOriginalVal,this.sheetVersionNumber);
        activeCells.put(cell.getId(), cell);
    }


    public EffectiveValue ref(EffectiveValue id, String IdThatCalledMe) {
        String theCellThatRefIsReferringTo = (String) id.getValue();
        CellImpl curr = getCell(theCellThatRefIsReferringTo);
        if (curr == null)
            throw new RuntimeException("No such cell, create it before referring to it.");
        curr.addAffectsOnId(IdThatCalledMe);
        return curr.getEffectiveValue(); //returns EffectiveValue
    }

    public CellImpl getCell(String cellId) {
       checkCellId(cellId);
        char letter = cellId.charAt(0); //taking the char
        int col = Character.getNumericValue(letter) - Character.getNumericValue('A'); //getting the col
        int row = Integer.parseInt(cellId.substring(1));
        if (col < 0 || row < 0 || row >= rowSize || col >= columnSize) {
            throw new IllegalArgumentException("The specified column or row number is invalid. Found: " + cellId + " please make sure that the CellImpl you refer to exists.");
        }
        CellImpl.setSpreadSheet(this);
        CellImpl cell = activeCells.get(cellId);
        if (cell == null) {
            throw new RuntimeException("No such cell, it contains nothing, before referring to it you should update its content.");
        }
        return cell;
    }

    private void checkCellId(String id) {
        if (!id.matches("^[A-Za-z]\\d+$")) {
            throw new IllegalArgumentException("Input must be in the format of a letter followed by one or more digits. Found: " + id);
        }
    }

    public void addCells(List<STLCell> cells) {
        CellImpl.setSpreadSheet(this);
        if(cells == null || cells.isEmpty()) {
            return;
        }
        CellImpl.setSpreadSheet(this);
        for (STLCell cell : cells) {
            CellImpl cellImpl = new CellImpl(cell);
            this.activeCells.put(cellImpl.getId(), cellImpl);
        }
    }

    public Map<String, CellImpl> getSTLCells() {
        return activeCells;
    }

    public Map<Integer, SpreadSheetImpl> getSheets() {
        return sheetMap;
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

    public void updateVersionNumber() {
        this.sheetVersionNumber++;
    }

    public SpreadSheetImpl getSheetBeforeChange() {
        return sheetBeforeChange;
    }
}

