package manager.impl;

import dto.*;
import checkfile.STLSheet;
import engine.Engine;
import manager.api.SheetManager;
import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.io.*;
import java.util.*;

import static checkfile.CheckForXMLFile.isXMLFile;
import static checkfile.CheckForXMLFile.readXMLFile;

public class SheetManagerImpl implements SheetManager, Serializable {

    private static final int MAX_ROWS = 50;
    private static final int MIN_ROWS_AND_COLS = 1;
    private static final int MAX_COLS = 20;

    private SpreadSheetImpl spreadSheet;
    private final Map<Integer, SheetDto> sheets = new HashMap<>();
    private String revertOriginalVal = null;
    private final PermissionManager permissionManager;

    public SheetManagerImpl(String Owner) {
        this.permissionManager = new PermissionManager();
        this.permissionManager.addOwner(Owner);
        this.spreadSheet = null;
    }

    public boolean havePermissionToEdit(String username) {
        return permissionManager.havePermissionToEdit(username);
    }

    public void setPermissionFinalDecision(String user, Engine.ApprovalStatus approvalStatus) {
        permissionManager.setFinalDecision(user, approvalStatus);
    }

    public void addPermissionRequest(String user, Engine.PermissionStatus status) {
        permissionManager.addPendingRequest(user, status);
    }

    public Map<String, PermissionDecision> getPendingPermissionsRequests() {
        return permissionManager.getPendingRequests();
    }

    public List<PermissionDecision> getPermissionHistory() {
        return permissionManager.getPermissionHistory();
    }

    public Map<String, PermissionDecision> getFinalizedPermissions() {
        return permissionManager.getFinalizedPermissions();
    }

    public String getSheetSize() {
        return spreadSheet.getColumnSize() + "x" + spreadSheet.getRowSize();
    }

    public boolean isOwner(String user) {
        return permissionManager.isOwner(user);
    }

    public int getSheetVersion() {
        return spreadSheet.getSheetVersionNumber();
    }

    public enum OperatorValue {
        OR_OPERATOR, AND_OPERATOR
    }

    public String Load(InputStream inputStream) {
        try {
            //take the data
            byte[] inputStreamBytes = inputStream.readAllBytes();
            //check if its xml
            if (!isXMLFile(new ByteArrayInputStream(inputStreamBytes))) {
                throw new IllegalArgumentException("Not a valid XML file");
            }
            //try to init
            STLSheet newSheet = readXMLFile(new ByteArrayInputStream(inputStreamBytes));
            initSheet(newSheet); //if init, newSheet != null.
            assert newSheet != null;
            return newSheet.getName();

        } catch (Exception e) {
            throw new IllegalArgumentException("Not a valid XML file: " + e.getMessage());
        }
    }

    public void initSheet(STLSheet stlSheet) {
        if (stlSheet == null) {
            throw new NullPointerException("STLSheet is null");
        }
        if (stlSheet.getSTLLayout().getRows() > MAX_ROWS) {
            throw new IllegalArgumentException("XML file inserted more than 50 rows");
        }
        if (stlSheet.getSTLLayout().getRows() < MIN_ROWS_AND_COLS) {
            throw new IllegalArgumentException("XML file inserted less than " + MIN_ROWS_AND_COLS + " rows");
        }
        if (stlSheet.getSTLLayout().getColumns() > MAX_COLS) {
            throw new IllegalArgumentException("XML file inserted contains more than 20 columns");
        }
        if (stlSheet.getSTLLayout().getColumns() < MIN_ROWS_AND_COLS) {
            throw new IllegalArgumentException("XML file inserted less than " + MIN_ROWS_AND_COLS + " columns");
        }
        this.spreadSheet = new SpreadSheetImpl(stlSheet);
        System.out.println("init Sheet =======> 100%");
        sheets.clear();
        sheets.put(this.spreadSheet.getSheetVersionNumber(), new SheetDto(this.spreadSheet));
    }

    public String getSheetName() {
        return this.spreadSheet.getSheetName();
    }

    @Override
    public SheetDto display() {
        return new SheetDto(this.spreadSheet);
    }

    @Override
    public CellDataDto showCell(String id) {
        return new CellDataDto(this.spreadSheet.getCellOrCreateIt(id));
    }

    @Override
    public SheetDto updateCell(String cellId, String value, boolean dynamically, String changedBy) {
        if (value != null && value.matches(".*\\S.*"))
            value = value.trim();
        try {
            this.spreadSheet.changeCell(cellId, value, !dynamically, changedBy);
            if (!dynamically)
                sheets.put(this.spreadSheet.getSheetVersionNumber(), new SheetDto(this.spreadSheet));
        } catch (Exception e) {
            this.spreadSheet = this.spreadSheet.getSheetBeforeChange(); //1 snapshot back
            throw e;
        }
        return new SheetDto(this.spreadSheet);
    }

    public SheetDto setOriginalValDynamically(String cellId, String newOriginalVal) {
        return updateCell(cellId, newOriginalVal, true, null);
    }

    public SheetDto finishedDynamicallyChangeFeature(String cellId) {
        SheetDto revertSheet = updateCell(cellId, this.revertOriginalVal, true, null);
        this.revertOriginalVal = null;
        return revertSheet;
    }

    public void saveCellValue(String cellId) {
        this.revertOriginalVal = spreadSheet.getCell(cellId).getOriginalValue();
    }

    public void savePositionToFile(String folderPath, String fileName) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, fileName.trim() + ".ser"); //trim in order to remove spaces
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }

    public SheetDto sort(String params, List<String> sortBy) {
        String[] cellIdentifiers = checkRangeParams(params);
        if (sortBy.isEmpty())
            throw new RuntimeException("You did not specify what rows should we sort for\nPlease provide this information and run the sorting function again.");
        SpreadSheetImpl spreadSheetCopy = this.spreadSheet.deepCopy();
        spreadSheetCopy.sort(cellIdentifiers, sortBy);
        return new SheetDto(spreadSheetCopy);
    }

    public SheetDto filter(String params, Map<String, Set<String>> filterBy, OperatorValue operatorValue) {
        String[] cellIdentifiers = checkRangeParams(params);
        if (filterBy.isEmpty())
            throw new RuntimeException("You did not specify what params should we filter for\nPlease provide this information and run the filter function again.");
        SpreadSheetImpl spreadSheetCopy = this.spreadSheet.deepCopy();
        spreadSheetCopy.filter(cellIdentifiers, filterBy, operatorValue);
        return new SheetDto(spreadSheetCopy);
    }

    private String[] checkRangeParams(String params) {
        if (params == null || !params.contains("..")) {
            throw new IllegalArgumentException("Invalid range format. Expected format: <Letter,Number>..<Letter,Number>");
        }
        String[] cellIdentifiers = params.split("\\.\\.");
        if (cellIdentifiers.length != 2) {
            throw new IllegalArgumentException("Invalid range format. Expected format: <Letter,Number>..<Letter,Number>");
        }
        return cellIdentifiers;
    }

    public void addRange(String name, String params) {
        String[] cellIdentifiers = checkRangeParams(params); //may result exception
        spreadSheet.addRange(name, cellIdentifiers[0], cellIdentifiers[1]); //may result exception
        //there is no need to revert to the last spreadsheet
    }

    public RangeDto getRangeDto(String id) {
        SheetDto sheet = this.display();
        return sheet.getRange(id);
    }

    public void deleteRange(String name) {
        this.spreadSheet.deleteRange(name);
    }

    public boolean containSheet() {
        return this.spreadSheet != null;
    }

    public Map<Integer, SheetDto> getSheets() {
        return sheets;
    }

    public SheetDto getSheet(int version) {
        return sheets.get(version);
    }

    public void setTextColor(String id, String selectedColor) {
        this.spreadSheet.getCellOrCreateIt(id).setTextColor(selectedColor);
    }

    public void setBackgroundColor(String id, String selectedColor) {
        this.spreadSheet.getCellOrCreateIt(id).setBackgroundColor(selectedColor);
    }

    public Set<CellDataDto> getSetOfCellsDtoDummyRange(String params) {
        String[] cellIdentifiers = checkRangeParams(params);
        Set<CellImpl> cells = spreadSheet.getSetOfCellsFromDummyRange(cellIdentifiers[0], cellIdentifiers[1]);
        Set<CellDataDto> cellsDto = new HashSet<>();
        for (CellImpl cell : cells) {
            cellsDto.add(new CellDataDto(cell));
        }
        return cellsDto;
    }

    public SheetManagerImpl deepCopy() {
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
            return (SheetManagerImpl) objectInputStream.readObject();

        } catch (NotSerializableException e) {
            System.err.println("A field is not serializable: " + e.getMessage());
            throw new RuntimeException("Failed to deep copy SheetManagerImpl due to non-serializable field", e);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deep copy SheetManagerImpl", e);
        }
    }
}
