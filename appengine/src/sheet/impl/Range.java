package sheet.impl;

import java.io.Serializable;
import java.util.Set;

public class Range implements Serializable {
    //from the way we create it inside the SpreadSheetImpl, the cells id are already uppercased and trimmed.
    private String name;
    private String topLeftCellId;
    private String bottomRightCellId;
    private Set<CellImpl> cellsInRange;

    public Range(String name, String topLeftCellId, String bottomRightCellId, Set<CellImpl> cellsInRange) {
        this.name = name;
        this.topLeftCellId = topLeftCellId;
        this.bottomRightCellId = bottomRightCellId;
        this.cellsInRange = cellsInRange;
    }

    public String getTopLeftCellId() {
        return topLeftCellId;
    }

    public String getBottomRightCellId() {
        return bottomRightCellId;
    }

    public String getName() {
        return name;
    }

    public Set<CellImpl> getRangeCells() {
        return cellsInRange;
    }

}
