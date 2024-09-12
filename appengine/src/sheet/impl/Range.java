package sheet.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Range implements Serializable {
    //from the way we create it inside the SpreadSheetImpl, the cells id are already uppercased and trimmed.
    private String name;
    private String topLeftCellId;
    private String bottomRightCellId;
    private Set<CellImpl> affectsOnCells;
    private Set<CellImpl> cellsInRange;

    public Range(String name, String topLeftCellId, String bottomRightCellId, Set<CellImpl> cellsInRange) {
        this.affectsOnCells = new HashSet<>();
        this.name = name;
        this.topLeftCellId = topLeftCellId;
        this.bottomRightCellId = bottomRightCellId;
        this.cellsInRange = cellsInRange;

    }

    public Set<String> getCellsThatTheRangeAffects(){
        Set<String> set = new HashSet<>();
        for(CellImpl cell : affectsOnCells){
            set.add(cell.getId());
        }
        return set;
    }

    public String getTopLeftCellId() {
        return topLeftCellId;
    }

    public String getBottomRightCellId() {
        return bottomRightCellId;
    }

    public void addAffectsOnCells(CellImpl cell) {
        affectsOnCells.add(cell);
    }

    public void removeAffectsOnCells(CellImpl cell) {
        affectsOnCells.remove(cell);
    }

    public String getName() {
        return name;
    }

    public Set<CellImpl> getRangeCells() {
        return cellsInRange;
    }

}
