package http.dto;

import java.util.Set;

public class CellDataDtoResp {
    private  int row;
    private  String col;
    private  String id;
    private  int lastChangeAt;
    private  Set<String> dependsOn;
    private  Set<String> affectsOn;
    private  String originalValue;
    private EffectiveValueDtoResp effectiveValue;
    private CellColorDtoResp cellColor;
    private  String changedBy;

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

    public EffectiveValueDtoResp getEffectiveValue() {
        return effectiveValue;
    }

    public CellColorDtoResp getCellColor() {
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