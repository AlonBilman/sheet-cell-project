package dto;

import sheet.api.EffectiveValue;
import sheet.impl.CellColor;
import sheet.impl.CellImpl;

import java.io.Serializable;
import java.util.Set;

public class CellDataDTO implements Serializable {
    private final int row;
    private final String col;
    private final String id;
    private final int lastChangeAt;
    private final Set<String> dependsOn;
    private final Set<String> affectsOn;
    private final String originalValue;
    private final EffectiveValue effectiveValue;
    private final CellColor cellColor;

    public CellDataDTO(CellImpl cell) {
        this.row = cell.getRow();
        this.col = cell.getCol();
        this.id = cell.getId();
        this.lastChangeAt = cell.getLastChangeAt();
        this.dependsOn = cell.getDependsOn();
        this.affectsOn = cell.getAffectsOn();
        this.originalValue = cell.getOriginalValue();
        this.effectiveValue = cell.getEffectiveValue();
        this.cellColor = new CellColor(cell.getCellColor());

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
}