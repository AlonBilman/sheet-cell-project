package sheet.impl;

import FileCheck.*;
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
    private final String sheetName;
    private int sheetVersionNumber;
    private SpreadSheetImpl sheetBeforeChange = null;


    public SpreadSheetImpl(STLSheet stlSheet) {
        CellImpl.setSpreadSheet(this);
        this.activeCells = new HashMap<>();
        this.sheetVersionNumber = 1;
        this.rowSize = stlSheet.getSTLLayout().getRows();
        this.columnSize = stlSheet.getSTLLayout().getColumns();
        this.colWidth = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();
        this.rowHeight = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        this.sheetName = stlSheet.getName();
        STLCells stlCells = stlSheet.getSTLCells();
        if (stlCells != null) {
            addCells(stlCells.getSTLCell());
        }
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
        if (cell == null) //meaning there is no cell like this activated
        {
            checkCellId(id);
            addCell(id.charAt(1) - '0', id.substring(0, 1), newOriginalVal);
        } else {
            cell.setOriginalValue(newOriginalVal);
        }
        updateVersionNumber();
    }

    public void addCell(int row, String col, String newOriginalVal) {
        CellImpl.setSpreadSheet(this);
        sheetBeforeChange = deepCopy();
        CellImpl cell = new CellImpl(row, col, newOriginalVal, this.sheetVersionNumber);
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
        if (col < 0 || row <= 0 || row >= rowSize || col >= columnSize) {
            throw new IllegalArgumentException("The specified column or row number is invalid. Inserted: " + cellId + "\nPlease make sure that the Cell slot you refer to exists.");
        }

        return activeCells.get(cellId);
    }

    private void checkCellId(String id) {
        if (!id.matches("^[A-Z]\\d+$")) {
            throw new IllegalArgumentException("Input must be in the format of a Capita letter followed by one or more digits. Found: " + id);
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
            dependencies.add(matcher.group(1));
        }
        return dependencies;
    }

    private void dfs(String cellId, Map<String, List<String>> dependencyGraph,
                     Set<String> visited, Set<String> inProcess,
                     List<STLCell> sortedCells, Map<String, STLCell> cellMap) {

        if (inProcess.contains(cellId)) {
            throw new IllegalStateException("Circular dependency detected!");
        }

        if (!visited.contains(cellId)) {
            inProcess.add(cellId);
            try {
                for (String dependentCellId : dependencyGraph.get(cellId)) {
                    dfs(dependentCellId, dependencyGraph, visited, inProcess, sortedCells, cellMap);
                }
                inProcess.remove(cellId);
                visited.add(cellId);
                sortedCells.add(cellMap.get(cellId));  //add the actual cell to the sorted list
            } catch (Exception e) {
                throw new RuntimeException("Problem with dependency of cell " + cellId + ":" + dependencyGraph.get(cellId));
            }
        }
    }

    public Map<String, CellImpl> getSTLCells() {
        return activeCells;
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