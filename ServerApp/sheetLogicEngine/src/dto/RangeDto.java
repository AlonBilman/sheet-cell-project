package dto;

import sheet.impl.CellImpl;
import sheet.impl.Range;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Set;

public class RangeDto implements Serializable {
    private final String name;
    private final Set<CellDataDto> cells;

    public RangeDto(Range range) {
        this.name = range.getName();
        //converting all the cells to cells dto
        Set<CellImpl> setOfRealCells = range.getRangeCells();
        Set<CellDataDto> setOfDtoCells = new HashSet<>();
        for (CellImpl cell : setOfRealCells) {
            setOfDtoCells.add(new CellDataDto(cell));
        }
        cells = setOfDtoCells;
    }

    public String getName() {
        return name;
    }

    public Set<CellDataDto> getCells() {
        return cells;
    }
}
