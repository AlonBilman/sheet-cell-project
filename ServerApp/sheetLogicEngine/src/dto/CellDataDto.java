package dto;

import sheet.api.EffectiveValue;
import sheet.impl.CellColor;
import sheet.impl.CellImpl;
import sheet.impl.EffectiveValueImpl;

import java.io.Serializable;
import java.util.Set;

public class CellDataDto implements Serializable {
    private final int row;
    private final String col;
    private final String id;
    private final int lastChangeAt;
    private final Set<String> dependsOn;
    private final Set<String> affectsOn;
    private final String originalValue;
    private final EffectiveValueImpl effectiveValue;
    private final CellColor cellColor;
    private final String changedBy;

    public CellDataDto(CellImpl cell) {
        this.row = cell.getRow();
        this.col = cell.getCol();
        this.id = cell.getId();
        this.lastChangeAt = cell.getLastChangeAt();
        this.dependsOn = cell.getDependsOn();
        this.affectsOn = cell.getAffectsOn();
        this.originalValue = cell.getOriginalValue();
        this.effectiveValue = (EffectiveValueImpl) cell.getEffectiveValue();
        this.cellColor = new CellColor(cell.getCellColor());
        this.changedBy = cell.getChangedBy();
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

    public CellColor getCellColor() {
        return cellColor;
    }

    public int getRow() {
        return row;
    }

    public String getCol() {
        return col;
    }

    public String getChangedBy() {
        return changedBy;
    }
}