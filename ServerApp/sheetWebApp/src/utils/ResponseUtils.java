package utils;

import com.google.gson.Gson;
import dto.CellDataDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

public class ResponseUtils {

    private static final Gson GSON = new Gson();

    //write error response in JSON format
    public static void writeErrorResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        response.setStatus(status);
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, status);
        response.getWriter().write(GSON.toJson(errorResponse));
    }

    //write success response in JSON format
    public static void writeSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        if (data != null)
            response.getWriter().write(GSON.toJson(data));
    }

    private static class ErrorResponse {
        private final String error;
        private final int status;

        public ErrorResponse(String error, int status) {
            this.error = error;
            this.status = status;
        }

        public ErrorResponse() {
            this.error = null;
            this.status = 0;
        }

        public String getError() {
            return error;
        }

        public int getStatus() {
            return status;
        }
    }

    public static class RangeBody {
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

    public static class SortObj {
        private final String params;
        private final List<String> sortBy;

        public SortObj() {
            this.params = "";
            this.sortBy = new ArrayList<>();
        }

        public SortObj(String params, List<String> sortBy) {
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

    public static class FilterObj {
        private final String params;
        private final Map<String, Set<String>> filterBy;
        private final String operator;

        public FilterObj() {
            this.params = "";
            this.filterBy = new HashMap<>();
            this.operator = "";
        }

        public FilterObj(String params, Map<String, Set<String>> filterBy, String operator) {
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

    public static class Ranges {
        private final String xParams;
        private final String yParams;
        private final Set<CellDataDTO> XRange;
        private final Set<CellDataDTO> YRange;

        public Ranges(Set<CellDataDTO> XRange, Set<CellDataDTO> YRange) {
            this.XRange = XRange;
            this.YRange = YRange;
            xParams = "";
            yParams = "";
        }

        public Ranges(String xParams, String yParams) {
            this.xParams = xParams;
            this.yParams = yParams;
            this.XRange = new HashSet<>();
            this.YRange = new HashSet<>();
        }

        public Ranges() {
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
}
