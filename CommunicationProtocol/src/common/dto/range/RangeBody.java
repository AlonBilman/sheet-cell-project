package common.dto.range;

public class RangeBody {
    private String name;
    private String toAndFrom;

    RangeBody(String name, String toAndFrom) {
        this.name = name;
        this.toAndFrom = toAndFrom;
    }

    RangeBody(String name) {
        this.name = name;
        this.toAndFrom = "";
    }

    public String getName() {
        return name;
    }

    public String getToAndFrom() {
        return toAndFrom;
    }
}


