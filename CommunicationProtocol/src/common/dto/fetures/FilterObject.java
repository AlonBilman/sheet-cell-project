package common.dto.fetures;

import java.util.*;

public class FilterObject {
    private final String params;
    private final Map<String, Set<String>> filterBy;
    private final String operator;

    public FilterObject() {
        this.params = "";
        this.filterBy = new HashMap<>();
        this.operator = "";
    }

    public FilterObject(String params, Map<String, Set<String>> filterBy, String operator) {
        this.params = params;
        this.filterBy = filterBy;
        this.operator = operator;
    }

    public String getParams() {
        return this.params;
    }

    public String getOperator() {
        return this.operator;
    }

    public Map<String, Set<String>> getFilterBy() {
        return this.filterBy;
    }
}