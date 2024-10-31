package common.dto.fetures;

import java.util.*;

public  class SortObject {
    private final String params;
    private final List<String> sortBy;

    public SortObject() {
        this.params = "";
        this.sortBy = new ArrayList<>();
    }

    public SortObject(String params, List<String> sortBy) {
        this.params = params;
        this.sortBy = sortBy;
    }

    public String getParams() {
        return params;
    }

    public List<String> getSortBy() {
        return sortBy;
    }
}
