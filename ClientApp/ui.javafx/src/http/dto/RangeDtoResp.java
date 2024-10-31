package http.dto;

import java.util.Set;

public class RangeDtoResp {
    private  String name;
    private  Set<CellDataDtoResp> cells;

    public String getName() {
        return name;
    }

    public Set<CellDataDtoResp> getCells() {
        return cells;
    }
}
