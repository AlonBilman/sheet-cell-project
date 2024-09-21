package sheet.impl;

import checkfile.*;
import expression.api.ObjType;
import sheet.api.EffectiveValue;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private Set<String> cellsToCalcAfterLoadFaze = null;

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
        STLRanges stlRanges = stlSheet.getSTLRanges();
        cellsToCalcAfterLoadFaze = new HashSet<>();
        if (stlCells != null)
            //building all the cells from the xml
            addCells(stlCells.getSTLCell(), stlRanges);
        if (stlRanges != null)
            //building all the ranges from the xml
            buildRanges(stlRanges.getSTLRange());
        //again after loading all the ranges - recalculate cells that use ranges
        for (String id : cellsToCalcAfterLoadFaze)
            activeCells.get(id).calculateEffectiveValue();
        this.sheetVersionNumber = 1; //adding all the cells -> sheetV = 1
        cellsToCalcAfterLoadFaze = null;
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

    private void buildRanges(List<STLRange> stlRanges) {
        for (STLRange range : stlRanges) {
            String rangeName = range.getName();
            STLBoundaries boundaries = range.getSTLBoundaries();
            String fromCell = boundaries.getFrom();
            String toCell = boundaries.getTo();
            addRange(rangeName, fromCell, toCell);
        }
    }

    private void initNullCell(String id, String newOriginalVal) {
        checkCellId(id);
        addCell(Integer.parseInt(id.substring(1)), id.substring(0, 1), newOriginalVal);
    }

    public EffectiveValue ref(EffectiveValue id, String IdThatCalledMe) {
        String theCellThatRefIsReferringTo = (String) id.getValue();
        CellImpl curr = getCell(theCellThatRefIsReferringTo);
        //create it ! (used to return an error).we're here after string check so the CellId is valid
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

    private void addCells(List<STLCell> cells, STLRanges stlRanges) {
        CellImpl.setSpreadSheet(this);
        List<STLRange> ranges = new ArrayList<>(); //so it won't be null
        if (stlRanges != null)
            ranges = stlRanges.getSTLRange();

        if (cells == null || cells.isEmpty())
            return;

        List<STLCell> sortedCells = topologicalSort(cells, ranges); //could fail duo to circular dep
        for (STLCell cell : sortedCells) {
            CellImpl cellImpl = new CellImpl(cell);
            this.activeCells.put(cellImpl.getId(), cellImpl);
        }
    }

    private List<STLCell> topologicalSort(List<STLCell> cells, List<STLRange> ranges) {
        List<STLCell> sortedCells = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> inProcess = new HashSet<>();
        Map<String, STLCell> cellMap = new HashMap<>();
        Map<String, List<String>> dependencyGraph = dependencyGraphBuild(cells, cellMap, ranges);
        for (String cellId : dependencyGraph.keySet()) {
            if (!visited.contains(cellId)) {
                dfs(cellId, dependencyGraph, visited, inProcess, sortedCells, cellMap);
            }
        }
        return sortedCells;
    }

    private Map<String, List<String>> dependencyGraphBuild(List<STLCell> cells, Map<String, STLCell> cellMap, List<STLRange> ranges) {
        Map<String, List<String>> dependencyGraph = new HashMap<>();
        for (STLCell cell : cells) {
            String cellId = cell.getColumn() + cell.getRow();
            cellId = cellId.trim().toUpperCase();
            cellMap.put(cellId, cell);  //map cell ID to STLCell object
            List<String> dependencies = extractDependencies(cell, ranges, cellId);
            dependencyGraph.put(cellId, dependencies);
        }
        return dependencyGraph;
    }

    private List<String> extractCellIdFromRange(STLRange range) {
        List<String> list = new ArrayList<>();
        int startRow = getNumberRow(range.getSTLBoundaries().getFrom());
        int endRow = getNumberRow(range.getSTLBoundaries().getTo());
        char startCol = getLetterCol(range.getSTLBoundaries().getFrom());
        char endCol = getLetterCol(range.getSTLBoundaries().getTo());
        for (int row = startRow; row <= endRow; row++) {
            for (char col = startCol; col <= endCol; col++) {
                String cellId = "" + col + row;
                list.add(cellId);
            }
        }
        return list;
    }

    private List<String> extractDependencies(STLCell cell, List<STLRange> ranges, String cellId) {
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
        //check for range references
        Pattern rangePattern = Pattern.compile("\\{(AVERAGE|SUM),([^}]+)}");
        Matcher rangeMatcher = rangePattern.matcher(expression);
        while (rangeMatcher.find()) {
            String rangeName = rangeMatcher.group(2).trim();
            ranges.stream()
                    .filter(r -> rangeName.equals(r.getName()))
                    .findFirst().ifPresent(range -> dependencies.addAll(extractCellIdFromRange(range)));
            cellsToCalcAfterLoadFaze.add(cellId);
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
                if (dependencyGraph.containsKey(cellId)) {
                    //IF NOT! meaning I don't care about the dependency of it, the xml did not create it ->
                    //it's an empty cell so ill ignore it.
                    for (String dependentCellId : dependencyGraph.get(cellId)) {
                        dfs(dependentCellId, dependencyGraph, visited, inProcess, sortedCells, cellMap);
                    }
                    inProcess.remove(cellId);
                    visited.add(cellId);
                    sortedCells.add(cellMap.get(cellId));
                }

            } catch (RuntimeException e) {
                if (e.getMessage().contains("Circular dependency detected!")) {
                    throw new RuntimeException(e.getMessage() + " -> " + cellId);
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
    }

    private void checkRangeParams(String p1, String p2) {
        checkCellId(p1);
        checkCellId(p2);
    }

    public void addRange(String rangeName, String topLeftCellId, String bottomRightCellId) {
        topLeftCellId = cleanId(topLeftCellId);
        bottomRightCellId = cleanId(bottomRightCellId);
        checkRangeParams(topLeftCellId, bottomRightCellId);
        if (activeRanges.containsKey(rangeName)) {
            throw new IllegalArgumentException("Range name already exists.\n\"" + rangeName + "\" is taken");
        }
        Set<CellImpl> cells = getSetOfCellsForRange(topLeftCellId, bottomRightCellId);
        Range range = new Range(rangeName, topLeftCellId, bottomRightCellId, cells);
        activeRanges.put(rangeName, range);
    }

    //will be used in the functions
    public Range getRange(String name) {
        return activeRanges.get(name);
    }

    public Set<CellImpl> getSetOfCellsForRange(String topLeftCellId, String bottomRightCellId) {
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
        if (cellsInRange.isEmpty()) {
            throw new RuntimeException("The cells Id you've given did not match the format.\n" +
                    "In order to create a range please provide first topLeftCellId and bottomRightCellId - in this order");
        }
        return cellsInRange;
    }

    public void deleteRange(String name) {
        if (activeRanges.containsKey(name)) {
            Range range = activeRanges.get(name);
            Set<String> setId = range.getCellsThatTheRangeAffects();
            if (setId.isEmpty()) {
                activeRanges.remove(name);
            } else throw new RuntimeException("Cannot delete this range.\nIt affects this cell(s): " + setId);
        } else throw new IllegalArgumentException("No such range - " + name);
    }

    public Map<String, CellImpl> getActiveCells() {
        return activeCells;
    }

    public Map<String, Range> getActiveRanges() {
        return activeRanges;
    }

    private Double getColumnValue(Set<CellImpl> cells, String column) {
        for (CellImpl cell : cells) {
            if (cell.getCol().equals(column) && cell.getEffectiveValue().getObjType().equals(ObjType.NUMERIC)) {
                return (Double) cell.getEffectiveValue().getValue();
            }
        }
        return null;
    }

    public void sort(String[] params, List<String> sortBy) {
        params[0] = cleanId(params[0]);
        params[1] = cleanId(params[1]);
        checkRangeParams(params[0], params[1]);
        sortBy = cleanListOfRow(sortBy);
        Map<Integer, Set<CellImpl>> rowMap = rowMapBuilder(params[0], params[1]);
        List<Map.Entry<Integer, Set<CellImpl>>> rowEntries = getEntriesSorted(sortBy, rowMap);
        int startFromRow = rowMap.keySet().stream().min(Integer::compareTo).orElse(0);
        for (Map.Entry<Integer, Set<CellImpl>> rowEntry : rowEntries) {
            Set<CellImpl> cells = rowEntry.getValue();
            for (CellImpl cell : cells) {
                cell.setRow(startFromRow);
                activeCells.put(cell.getId(), cell);
            }
            startFromRow++;
        }
    }

    private List<Map.Entry<Integer, Set<CellImpl>>> getEntriesSorted(List<String> sortBy, Map<Integer, Set<CellImpl>> rowMap) {
        List<Map.Entry<Integer, Set<CellImpl>>> rowEntries = new ArrayList<>(rowMap.entrySet());

        rowEntries.sort((entry1, entry2) -> {
            Set<CellImpl> row1Cells = entry1.getValue();
            Set<CellImpl> row2Cells = entry2.getValue();

            for (String column : sortBy) {
                Double value1 = getColumnValue(row1Cells, column);
                Double value2 = getColumnValue(row2Cells, column);
                int comparison = compareValues(value1, value2);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return 0;
        });
        return rowEntries;
    }

    private List<String> cleanListOfRow(List<String> sortBy) {
        checkColList(sortBy);
        return sortBy.stream()
                .map(this::cleanId)
                .collect(Collectors.toList());
        //I'm in love with this type of coding...!!
    }

    private void checkColList(List<String> sortBy) {
        for (String col : sortBy) {
            if (!col.matches("^[A-Za-z]$"))
                throw new IllegalArgumentException("Input must be a single letter representing a column. Found: " + col);
            char letter = col.charAt(0);
            int colInt = Character.getNumericValue(letter) - Character.getNumericValue('A');
            if (colInt < 0 || colInt > columnSize - 1) {
                throw new IllegalArgumentException("The specified column is invalid. Inserted: " + col + "\nPlease make sure the column exists.");
            }
        }
    }

    private Map<Integer, Set<CellImpl>> rowMapBuilder(String from, String to) {
        Map<Integer, Set<CellImpl>> rowMap = new HashMap<>();
        Set<CellImpl> setOfCells = getSetOfCellsForRange(from, to);

        for (CellImpl cell : setOfCells) {
            Integer rowKey = cell.getRow();
            rowMap.computeIfAbsent(rowKey, k -> new HashSet<>()).add(cell);
        }
        return rowMap;
    }

    private int compareValues(Double value1, Double value2) {
        if (value2 == null && value1 == null) {
            return 0;
        } else if (value2 == null) {
            return -1;
        } else if (value1 == null) {
            return 1;
        }
        return Double.compare(value1, value2);
    }

    public void filter(String[] params, Map<String, Set<String>> filterBy) {
        params[0] = cleanId(params[0]);
        params[1] = cleanId(params[1]);
        checkRangeParams(params[0], params[1]);
        checkColList(new ArrayList<>(filterBy.keySet()));
        Map<Integer, Set<CellImpl>> rowMap = rowMapBuilder(params[0], params[1]);
        List<Set<CellImpl>> matchingRows = new ArrayList<>(); //in order to preserve the order!

        for (Map.Entry<Integer, Set<CellImpl>> rowEntry : rowMap.entrySet()) {
            Set<CellImpl> cells = rowEntry.getValue();
            boolean rowMatches = false;
            for (CellImpl cell : cells) {
                String column = cell.getCol();
                String cellValue = cell.getEffectiveValue().getValue().toString();
                if (filterBy.containsKey(column)) {
                    Set<String> filterValues = filterBy.get(column);
                    if (filterValues.contains(cellValue)) {
                        rowMatches = true;
                        break;
                    }
                }
            }
            if (rowMatches)
                matchingRows.add(cells);
            else {
                for (CellImpl cell : rowEntry.getValue())
                    activeCells.remove(cell.getId()); //we dont need it.
            }
        }

        int startFromRow = rowMap.keySet().stream().min(Integer::compareTo).orElse(0);
        for (Set<CellImpl> matchingRow : matchingRows) {
            for (CellImpl cell : matchingRow) {
                //kind of removing the curr position, just putting there an empty cell. a bit ugly but it works.
                activeCells.remove(cell.getId());
                cell.setRow(startFromRow); //moving the cell upwards
                activeCells.put(cell.getId(), cell);
            }
            startFromRow++;
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