package expression.api;

public enum ErrorValues {
    STRING_ERROR("!UNDEFINED!"),
    NUMERIC_ERROR("NaN"),
    BOOLEAN_ERROR("UNKNOWN"),
    EMPTY("");

    private final String errorMessage;

    ErrorValues(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
