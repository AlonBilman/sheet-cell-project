package engineImpl;

import sheet.api.EffectiveValue;
import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EngineImpl implements Engine {
    public static class loadDTO {
        private final File loadedFile;

        public loadDTO(File newFile, File oldFile) {
            if (newFile != null && newFile.exists() && newFile.getName().endsWith(".xml")) {
                this.loadedFile = newFile;
            }
            else this.loadedFile = oldFile;
        }

        public File getLoadedFile() {
            return loadedFile;
        }

        public boolean isValid() {
            return loadedFile != null;
        }
    }

    @Override
    public loadDTO Load(File newFile, File oldFile) {
        return new loadDTO(newFile, oldFile);
    }
    public static class exitDTO{
      int exitStatus;
      public exitDTO(){
          exitStatus = 0;
      }

        public int getExitStatus() {
            return exitStatus;
        }
    }

    public static class sheetDTO {
        private final int rowSize;
        private final int colSize;
        private final int rowHeight;
        private final int colWidth;
        private final Map<String, CellDataDTO> activeCells;
        private final String sheetName;
        private final int sheetVersionNumber;
        private final Map<Integer, sheetDTO> sheetMap;


        // Constructors, getters, setters
        public sheetDTO(SpreadSheetImpl sheet) {
            this.rowSize = sheet.getRowSize();
            this.colSize = sheet.getColumnSize();
            this.rowHeight = sheet.getRowHeight();
            this.colWidth = sheet.getColWidth();
            this.activeCells = convertCells(sheet.getSTLCells());
            this.sheetName = sheet.getSheetName();
            this.sheetVersionNumber = sheet.getSheetVersionNumber();
            this.sheetMap = convertSheetMap(sheet.getSheetMap());
        }

        private Map<String, CellDataDTO> convertCells(Map<String, CellImpl> cellMap) {
            Map<String, CellDataDTO> convertedCells = new HashMap<>();
            for (Map.Entry<String, CellImpl> entry : cellMap.entrySet()) {
                convertedCells.put(entry.getKey(), new CellDataDTO(entry.getValue()));
            }
            return convertedCells;
        }

        // Helper method to convert sheet map
        private Map<Integer, sheetDTO> convertSheetMap(Map<Integer, SpreadSheetImpl> sheetMap) {
            Map<Integer, sheetDTO> convertedMap = new HashMap<>();
            for (Map.Entry<Integer, SpreadSheetImpl> entry : sheetMap.entrySet()) {
                convertedMap.put(entry.getKey(), new sheetDTO(entry.getValue()));
            }
            return convertedMap;
        }
        public int getRowSize() {
            return rowSize;
        }
        public int getColSize() {
            return colSize;
        }
        public int getRowHeight() {
            return rowHeight;
        }
        public int getColWidth() {
            return colWidth;
        }
        public Map<String, CellDataDTO> getActiveCells() {
            return activeCells;
        }
        public String getSheetName() {
            return sheetName;
        }
        public int getSheetVersionNumber() {
            return sheetVersionNumber;
        }
        public Map<Integer, sheetDTO> getSheetMap() {
            return sheetMap;
        }

    }

//    public class CellDataDTO {
//        private final int row;
//        private final String col;
//        private final String id;
//        private final int lastChangeAt;
//        private final Set<String> dependsOn;
//        private final Set<String> affectsOn;
//        private final String originalValue;
//        private final EffectiveValue effectiveValue;
//
//        // Constructor
//        public CellDataDTO(int row, String col, String id, int lastChangeAt, Set<String> dependsOn,
//                           Set<String> affectsOn, String originalValue, EffectiveValue effectiveValue) {
//            this.row = row;
//            this.col = col;
//            this.id = id;
//            this.lastChangeAt = lastChangeAt;
//            this.dependsOn = dependsOn;
//            this.affectsOn = affectsOn;
//            this.originalValue = originalValue;
//            this.effectiveValue = effectiveValue;
//        }
//
//        // Static factory method to create DTO from a CellImpl instance
//        public static CellDataDTO fromCellImpl(CellImpl cell) {
//            return new CellDataDTO(
//                    cell.getRow(),
//                    cell.getCol(),
//                    cell.getId(),
//                    cell.getLastChangeAt(),
//                    cell.getDependsOn(),
//                    cell.getAffectsOn(),
//                    cell.getOriginalValue(),
//                    cell.getEffectiveValue()
//            );
//        }
//
//        // Getters
//        public int getRow() {
//            return row;
//        }
//
//        public String getCol() {
//            return col;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public int getLastChangeAt() {
//            return lastChangeAt;
//        }
//
//        public Set<String> getDependsOn() {
//            return dependsOn;
//        }
//
//        public Set<String> getAffectsOn() {
//            return affectsOn;
//        }
//
//        public String getOriginalValue() {
//            return originalValue;
//        }
//
//        public EffectiveValue getEffectiveValue() {
//            return effectiveValue;
//        }
//    }
    public static class CellDataDTO {
        private final int row;
        private final String col;
        private final String id;
        private final int lastChangeAt;
        private final Set<String> dependsOn;
        private final Set<String> affectsOn;
        private final String originalValue;
        private final EffectiveValue effectiveValue;
        private static SpreadSheetImpl lastUpdatedSpreadSheet;

        public CellDataDTO(CellImpl cell) {
            this.row = cell.getRow();
            this.col = cell.getCol();
            this.id = cell.getId();
            this.lastChangeAt = cell.getLastChangeAt();
            this.dependsOn = cell.getDependsOn();
            this.affectsOn = cell.getAffectsOn();
            this.originalValue = cell.getOriginalValue();
            this.effectiveValue = cell.getEffectiveValue();
        }
        public int getRow() {
            return row;
        }
        public String getCol() {
            return col;
        }
        public String getId() {
            return id;
        }
        public int getLastChangeAt() {
            return lastChangeAt;
        }
        public Set<String> getDependsOn() {
            return dependsOn;
        }
        public Set<String> getAffectsOn() {
            return affectsOn;
        }
        public String getOriginalValue() {
            return originalValue;
        }
        public EffectiveValue getEffectiveValue() {
            return effectiveValue;
        }
        public SpreadSheetImpl getLastUpdatedSpreadSheet() {
            return lastUpdatedSpreadSheet;
        }
    }
    @Override
    public EngineImpl.loadDTO Load() {
        return null;
    }

    @Override
    public  EngineImpl.sheetDTO Display(SpreadSheetImpl sheet) {
            return new sheetDTO(sheet);
    }

    @Override
    public  EngineImpl.CellDataDTO showCell(CellImpl cell) {
        return new CellDataDTO(cell);
    }

    @Override
    public sheetDTO updateCell(SpreadSheetImpl sheet) {
        return null;
    }

    @Override
    public sheetDTO showVersions(SpreadSheetImpl sheet) {
        return new sheetDTO(sheet);
    }

    @Override
    public exitDTO exitSystem() {
        return new exitDTO();
    }
}
