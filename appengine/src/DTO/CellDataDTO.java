package DTO;

import sheet.api.EffectiveValue;
import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.util.Set;

public class CellDataDTO {
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