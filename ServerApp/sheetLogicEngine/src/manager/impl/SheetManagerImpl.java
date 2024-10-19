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
    private final Map<Integer, sheetDTO> sheets = new HashMap<>();
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

    public Map<String, PermissionDecision> getFinalizedPermissions() {
        return permissionManager.getFinalizedPermissions();
    }

    public String getSheetSize() {
        return spreadSheet.getColumnSize() + "x" + spreadSheet.getRowSize();
    }

    public boolean isOwner(String user) {
        return permissionManager.isOwner(user);
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
                throw new IllegalArgumentException("XML is not valid");
            }
            //try to init
            STLSheet newSheet = readXMLFile(new ByteArrayInputStream(inputStreamBytes));
            initSheet(newSheet); //if init, newSheet != null.
            assert newSheet != null;
            return newSheet.getName();

        } catch (Exception e) {
            throw new IllegalArgumentException("XML is not valid: " + e.getMessage());
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
        sheets.put(this.spreadSheet.getSheetVersionNumber(), new sheetDTO(this.spreadSheet));
    }

    public String getSheetName() {
        return this.spreadSheet.getSheetName();
    }

    @Override
    public sheetDTO Display() {
        return new sheetDTO(this.spreadSheet);
    }

    @Override
    public CellDataDTO showCell(String id) {
        return new CellDataDTO(this.spreadSheet.getCellOrCreateIt(id));
    }

    @Override
    public sheetDTO updateCell(String cellId, String value, boolean dynamically) {
        if (value != null && value.matches(".*\\S.*"))
            value = value.trim();
        try {
            this.spreadSheet.changeCell(cellId, value, !dynamically);
            if (!dynamically)
                sheets.put(this.spreadSheet.getSheetVersionNumber(), new sheetDTO(this.spreadSheet));
        } catch (Exception e) {
            this.spreadSheet = this.spreadSheet.getSheetBeforeChange(); //1 snapshot back
            throw e;
        }
        return new sheetDTO(this.spreadSheet);
    }

    public sheetDTO setOriginalValDynamically(String cellId, String newOriginalVal) {
        return updateCell(cellId, newOriginalVal, true);
    }

    public sheetDTO finishedDynamicallyChangeFeature(String cellId) {
        sheetDTO revertSheet = updateCell(cellId, this.revertOriginalVal, true);
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

    public sheetDTO sort(String params, List<String> sortBy) {
        String[] cellIdentifiers = checkRangeParams(params);
        if (sortBy.isEmpty())
            throw new RuntimeException("You did not specify what rows should we sort for\nPlease provide this information and run the sorting function again.");
        SpreadSheetImpl spreadSheetCopy = this.spreadSheet.deepCopy();
        spreadSheetCopy.sort(cellIdentifiers, sortBy);
        return new sheetDTO(spreadSheetCopy);
    }

    public sheetDTO filter(String params, Map<String, Set<String>> filterBy, OperatorValue operatorValue) {
        String[] cellIdentifiers = checkRangeParams(params);
        if (filterBy.isEmpty())
            throw new RuntimeException("You did not specify what params should we filter for\nPlease provide this information and run the filter function again.");
        SpreadSheetImpl spreadSheetCopy = this.spreadSheet.deepCopy();
        spreadSheetCopy.filter(cellIdentifiers, filterBy, operatorValue);
        return new sheetDTO(spreadSheetCopy);
    }

    public static SheetManagerImpl resumePositionToEngine(String filePath, String fileName) throws IOException, ClassNotFoundException {
        File file = new File(filePath + "\\" + fileName.trim() + ".ser");
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (SheetManagerImpl) ois.readObject();
        }
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

    public RangeDTO getRangeDto(String id) {
        sheetDTO sheet = this.Display();
        return sheet.getRange(id);
    }

    public void deleteRange(String name) {
        this.spreadSheet.deleteRange(name);
    }

    public boolean containSheet() {
        return this.spreadSheet != null;
    }

    public Map<Integer, sheetDTO> getSheets() {
        return sheets;
    }

    public sheetDTO getSheet(int version) {
        return sheets.get(version);
    }

    public void setTextColor(String id, String selectedColor) {
        this.spreadSheet.getCellOrCreateIt(id).setTextColor(selectedColor);
    }

    public void setBackgroundColor(String id, String selectedColor) {
        this.spreadSheet.getCellOrCreateIt(id).setBackgroundColor(selectedColor);
    }

    public Set<CellDataDTO> getSetOfCellsDtoDummyRange(String params) {
        String[] cellIdentifiers = checkRangeParams(params);
        Set<CellImpl> cells = spreadSheet.getSetOfCellsFromDummyRange(cellIdentifiers[0], cellIdentifiers[1]);
        Set<CellDataDTO> cellsDto = new HashSet<>();
        for (CellImpl cell : cells) {
            cellsDto.add(new CellDataDTO(cell));
        }
        return cellsDto;
    }
}
