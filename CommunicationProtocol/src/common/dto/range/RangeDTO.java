package common.dto.range;

import common.dto.cell.CellDataDTO;
import sheet.impl.CellImpl;
import sheet.impl.Range;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RangeDTO implements Serializable {
    private final String name;
    private final Set<CellDataDTO> cells;

    public RangeDTO(Range range) {
        this.name = range.getName();
        //converting all the cells to cells dto
        Set<CellImpl> setOfRealCells = range.getRangeCells();
        Set<CellDataDTO> setOfDtoCells = new HashSet<>();
        for (CellImpl cell : setOfRealCells) {
            setOfDtoCells.add(new CellDataDTO(cell));
        }
        cells = setOfDtoCells;
    }

public String getName() {
    return name;
}

public Set<CellDataDTO> getCells() {
    return cells;
}
}
