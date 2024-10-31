package common.dto.range;

import common.dto.cell.CellDataDTO;

import java.util.*;

public class RangesForCharts {
    private final String xParams;
    private final String yParams;
    private final Set<CellDataDTO> XRange;
    private final Set<CellDataDTO> YRange;

    public RangesForCharts(Set<CellDataDTO> XRange, Set<CellDataDTO> YRange) {
        this.XRange = XRange;
        this.YRange = YRange;
        xParams = "";
        yParams = "";
    }

    public RangesForCharts(String xParams, String yParams) {
        this.xParams = xParams;
        this.yParams = yParams;
        this.XRange = new HashSet<>();
        this.YRange = new HashSet<>();
    }

    public RangesForCharts() {
        this.XRange = new HashSet<>();
        this.YRange = new HashSet<>();
        xParams = "";
        yParams = "";
    }

    public String getXParams() {
        return xParams;
    }

    public String getYParams() {
        return yParams;
    }

    public Set<CellDataDTO> getXRange() {
        return XRange;
    }

    public Set<CellDataDTO> getYRange() {
        return YRange;
    }

    public void setXRange(Set<CellDataDTO> XRange) {
        this.XRange.clear();
        this.XRange.addAll(XRange);
    }

    public void setYRange(Set<CellDataDTO> YRange) {
        this.YRange.clear();
        this.YRange.addAll(YRange);
    }

}


