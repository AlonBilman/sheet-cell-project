package sheet.impl;

import checkfile.STLCell;
import checkfile.STLCells;
import checkfile.STLSheet;
import sheet.api.EffectiveValue;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpreadSheetImpl implements Serializable {
    private final int rowSize;
    private final int columnSize;
    private final int colWidth;
    private final int rowHeight;
    private Map<String, CellImpl> activeCells;
    private Map<String, Range> activeRanges;
    private final String sheetName;
    private int sheetVersionNumber;
    private SpreadSheetImpl sheetBeforeChange = null;


    public SpreadSheetImpl(STLSheet stlSheet) {
        CellImpl.setSpreadSheet(this);
        this.activeCells = new HashMap<>();
        this.activeRanges = new HashMap<>();
        this.sheetVersionNumber = 0; //starting with 0
        this.rowSize = stlSheet.getSTLLayout().getRows();
        this.columnSize = stlSheet.getSTLLayout().getColumns();
        this.colWidth = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();
        this.rowHeight = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        this.sheetName = stlSheet.getName();
        STLCells stlCells = stlSheet.getSTLCells();
        if (stlCells != null) {
            addCells(stlCells.getSTLCell());
        }
        this.sheetVersionNumber = 1; //adding all the cells -> sheetV = 1
        sheetBeforeChange = deepCopy();
    }

    public SpreadSheetImpl deepCopy() {
        try {
            //serialize
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();

            //deserialize
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

    public void changeCell(String id, String newOriginalVal) {
        sheetBeforeChange = deepCopy();
        CellImpl.setSpreadSheet(this);
        checkCellId(id);
        CellImpl cell = getCell(id);
        if (cell == null) { //meaning there is no cell like this activated
            initNullCell(id, newOriginalVal);
            cell = getCell(id);
        }
        cell.setOriginalValue(newOriginalVal);
        updateVersionNumber();
    }

    public void addCell(int row, String col, String newOriginalVal) {
        CellImpl.setSpreadSheet(this);
        sheetBeforeChange = deepCopy();
        col = col.toUpperCase();
        CellImpl cell = new CellImpl(row, col, newOriginalVal, this.sheetVersionNumber);
        activeCells.put(cell.getId(), cell);
    }

    private void initNullCell(String id, String newOriginalVal) {
        checkCellId(id);
        addCell(Integer.parseInt(id.substring(1)), id.substring(0, 1), newOriginalVal);
    }

    public EffectiveValue ref(EffectiveValue id, String IdThatCalledMe) {
        String theCellThatRefIsReferringTo = (String) id.getValue();
        CellImpl curr = getCell(theCellThatRefIsReferringTo);
        // create it ! (used to return an error).we're here after string check so the CellId is valid
        if (curr == null) {
            initNullCell(theCellThatRefIsReferringTo, null);
            curr = getCell(theCellThatRefIsReferringTo);
        }
        curr.addAffectsOnId(IdThatCalledMe);
        return curr.getEffectiveValue(); //returns EffectiveValue
    }


    public String cleanId(String cellId) {
        return cellId.trim().toUpperCase();
    }

    public CellImpl getCell(String cellId) {
        cellId = cleanId(cellId);
        checkCellId(cellId);
        return activeCells.get(cellId);
    }

    public CellImpl getCellOrCreateIt(String cellId) {
        cellId = cleanId(cellId);
        CellImpl curr = getCell(cellId);
        if (curr == null) {
            initNullCell(cellId, null);
        }
        return activeCells.get(cellId);
    }

    private char getLetterCol(String id) {
        return Character.toUpperCase(id.charAt(0));
    }

    private int getNumberRow(String id) {
        return Integer.parseInt(id.substring(1));
    }

    private void checkCellId(String id) {
        if (!id.matches("^[A-Za-z]\\d+$")) {
            throw new IllegalArgumentException("Input must be in the format of a letter followed by one or more digits. Found: " + id);
        }
        char letter = getLetterCol(id);//taking the char
        int col = Character.getNumericValue(letter) - Character.getNumericValue('A'); //getting the col
        int row = getNumberRow(id);
        if (col < 0 || row <= 0 || row > rowSize || col > columnSize - 1) {
            throw new IllegalArgumentException("The specified column or row number is invalid. Inserted: " + id + "\nPlease make sure that the Cell slot you refer to exists.");
        }
    }

    private void addCells(List<STLCell> cells) {
        CellImpl.setSpreadSheet(this);
        if (cells == null || cells.isEmpty()) {
            return;
        }
        List<STLCell> sortedCells = topologicalSort(cells); //could fail duo to circular dep
        for (STLCell cell : sortedCells) {
            CellImpl cellImpl = new CellImpl(cell);
            this.activeCells.put(cellImpl.getId(), cellImpl);
        }
    }

    private List<STLCell> topologicalSort(List<STLCell> cells) {
        List<STLCell> sortedCells = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> inProcess = new HashSet<>();
        Map<String, STLCell> cellMap = new HashMap<>();
        Map<String, List<String>> dependencyGraph = dependancyGraphBuild(cells, cellMap);
        for (String cellId : dependencyGraph.keySet()) {
            if (!visited.contains(cellId)) {
                dfs(cellId, dependencyGraph, visited, inProcess, sortedCells, cellMap);
            }
        }
        return sortedCells;
    }

    private Map<String, List<String>> dependancyGraphBuild(List<STLCell> cells, Map<String, STLCell> cellMap) {
        Map<String, List<String>> dependencyGraph = new HashMap<>();
        for (STLCell cell : cells) {
            String cellId = cell.getColumn() + cell.getRow();
            cellId = cellId.trim().toUpperCase();
            cellMap.put(cellId, cell);  //map cell ID to STLCell object
            List<String> dependencies = extractDependencies(cell);
            dependencyGraph.put(cellId, dependencies);
        }
        return dependencyGraph;
    }

    private List<String> extractDependencies(STLCell cell) {
        List<String> dependencies = new ArrayList<>();
        String expression = cell.getSTLOriginalValue();
        Pattern pattern = Pattern.compile("\\{REF,([^}]+)}");
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            //trim if someone type id with space or something
            String id = matcher.group(1).trim().toUpperCase();
            checkCellId(id);
            dependencies.add(id);
        }
        return dependencies;
    }

    /* this dfs also contain checks for cells that did not initiate and circular dep
     (in order to cut the topologicalSort that does not work with circular graphs)
    */
    private void dfs(String cellId, Map<String, List<String>> dependencyGraph,
                     Set<String> visited, Set<String> inProcess,
                     List<STLCell> sortedCells, Map<String, STLCell> cellMap) {

        if (inProcess.contains(cellId)) {
            throw new IllegalStateException("Circular dependency detected! Trace: " + cellId);
        }

        if (!visited.contains(cellId)) {
            inProcess.add(cellId);
            try {
                if (!dependencyGraph.containsKey(cellId)) {
                    throw new RuntimeException("No such cell - " + cellId + ". Create it in order to refer it");
                }

                for (String dependentCellId : dependencyGraph.get(cellId)) {
                    dfs(dependentCellId, dependencyGraph, visited, inProcess, sortedCells, cellMap);
                }

                inProcess.remove(cellId);
                visited.add(cellId);
                sortedCells.add(cellMap.get(cellId));

            } catch (RuntimeException e) {
                if (e.getMessage().contains("Circular dependency detected!")) {
                    throw new RuntimeException(e.getMessage() + " -> " + cellId);
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    public void addRange(String rangeName, String topLeftCellId, String bottomRightCellId) {
        topLeftCellId = cleanId(topLeftCellId);
        bottomRightCellId = cleanId(bottomRightCellId);
        checkCellId(topLeftCellId);
        checkCellId(bottomRightCellId);
        if (activeRanges.containsKey(rangeName)) {
            throw new IllegalArgumentException("Range name already exists.\n\"" + rangeName + "\" is taken");
        }
        Set<CellImpl> cells = getSetOfCellsForRange(topLeftCellId, bottomRightCellId);
        Range range = new Range(rangeName, topLeftCellId, bottomRightCellId, cells);
        activeRanges.put(rangeName, range);
    }
    //will be used in the functions
    public Range getRange(String name){
        return activeRanges.get(name);
    }

    private Set<CellImpl> getSetOfCellsForRange(String topLeftCellId, String bottomRightCellId) {
        Set<CellImpl> cellsInRange = new HashSet<>();
        //the borders
        int startRow = getNumberRow(topLeftCellId);
        int endRow = getNumberRow(bottomRightCellId);
        char startCol = getLetterCol(topLeftCellId);
        char endCol = getLetterCol(bottomRightCellId);
        //now I need to get all the cells in the borders
        for (int row = startRow; row <= endRow; row++) {
            for (char col = startCol; col <= endCol; col++) {
                String cellId = "" + col + row;
                CellImpl cell = getCellOrCreateIt(cellId);
                cellsInRange.add(cell);
            }
        }
        if(cellsInRange.isEmpty()) {
            throw new RuntimeException("The cells Id you've given did not match the format.\n" +
                    "In order to create a range please provide first topLeftCellId and bottomRightCellId - in this order");
        }
        return cellsInRange;
    }

    public void deleteRange(String name) {
        if(activeRanges.containsKey(name)){
            activeRanges.remove(name);
        } else throw new IllegalArgumentException("No such range - " + name);
    }

    public Map<String, CellImpl> getActiveCells() {
        return activeCells;
    }

    public Map<String, Range> getActiveRanges() {
        return activeRanges;
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

    public String getSheetName() {
        return sheetName;
    }

    public int getSheetVersionNumber() {
        return sheetVersionNumber;
    }

    public void updateVersionNumber() {
        ++this.sheetVersionNumber;
    }

    public SpreadSheetImpl getSheetBeforeChange() {
        return sheetBeforeChange;
    }


}