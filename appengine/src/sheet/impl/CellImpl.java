package sheet.impl;
import FileCheck.STLCell;
import expression.api.Expression;
import expression.impl.*;
import expression.impl.Number;
import expression.impl.function.CellReferenceFunc;
import expression.impl.function.ConcatFunction;
import expression.impl.function.PlusFunction;
import sheet.api.EffectiveValue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellImpl {
    private final int row;
    private final String col;
    private final String id;
    private int lastChangeAt;
    private Set<String> dependsOn;
    private Set<String> affectsOn;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private static SpreadSheetImpl currSpreadSheet;

    public CellImpl(int row, String col) {
        this.row = row;
        this.col = col;
        this.id = generateId(col, row);
        dependsOn = new HashSet<>();
        affectsOn = new HashSet<>();
    }

    public CellImpl(STLCell cell) {
        lastChangeAt = 1;
        this.row = cell.getRow();
        this.col = cell.getColumn();
        dependsOn = new HashSet<>();
        affectsOn = new HashSet<>();
        setOriginalValue(cell.getSTLOriginalValue());
        this.id = generateId(col, row);
    }

    public static void setSpreadSheet(SpreadSheetImpl spreadSheet) {
        currSpreadSheet = spreadSheet;
    }

    public void editCell(String newOriginalVal) {
        setOriginalValue(newOriginalVal);
    }

    public void calculateEffectiveValue() {
        if (originalValue != null && !originalValue.isEmpty()) {
            Expression expression = parseExpression(originalValue);
            this.effectiveValue = expression.eval();
        } else {
            this.effectiveValue = null;
        }
    }

    private Expression parseExpression(String originalValue) {
        if (originalValue.startsWith("{") && originalValue.endsWith("}")) {
            originalValue = originalValue.substring(1, originalValue.length() - 1);

            int firstCommaIndex = originalValue.indexOf(',');
            if (firstCommaIndex == -1) {
                throw new IllegalArgumentException("Invalid expression format: " + originalValue);
            }

            String functionName = originalValue.substring(0, firstCommaIndex);
            String arguments = originalValue.substring(firstCommaIndex + 1);

            List<Expression> parsedArguments = parseArguments(arguments);

            switch (functionName) {
                case "PLUS":
                    if (parsedArguments.size() != 2) {
                        throw new IllegalArgumentException("PLUS function requires two arguments.");
                    }
                    return new PlusFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "CONCAT":
                    if (parsedArguments.size() != 2) {
                        throw new IllegalArgumentException("CONCAT function requires two arguments.");
                    }
                    return new ConcatFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "REF":
                    if (parsedArguments.size() != 1) {
                        throw new IllegalArgumentException("REF function requires one argument.");
                    }
                    Expression argument = parsedArguments.get(0);
                    Expression res = new CellReferenceFunc(argument, currSpreadSheet, this.id);
                    String referencedId = argument.eval().getValue().toString();
                    dependsOn.add(referencedId);
                    return res;

                default:
                    throw new IllegalArgumentException("Unknown/Unsupported function: " + functionName);
            }
        } else {
            return parseSimpleValue(originalValue);
        }
    }

    private List<Expression> parseArguments(String arguments) {
        List<Expression> result = new ArrayList<>();
        int braceCount = 0;
        StringBuilder currentArgument = new StringBuilder();
        for (int i = 0; i < arguments.length(); i++) {
            char ch = arguments.charAt(i);
            if (ch == '{') {
                braceCount++;
            } else if (ch == '}') {
                braceCount--;
            }
            if (ch == ',' && braceCount == 0) {
                result.add(parseExpression(currentArgument.toString()));
                currentArgument.setLength(0);
            } else {
                currentArgument.append(ch);
            }
        }
        if (!currentArgument.isEmpty()) {
            result.add(parseExpression(currentArgument.toString().trim()));
        }
        return result;
    }

    private Expression parseSimpleValue(String value) {
        if (value.isEmpty() || value.matches(".*[^0-9].*"))
            return new Mystring(value);
        return new Number(Double.valueOf(value));
    }

    private String generateId(String col, int row) {
        char letter = col.charAt(0);
        return letter + String.valueOf(row);
    }

    private void removeDependsOn() {
        for (String dependsOnId : dependsOn) {
            CellImpl cell = currSpreadSheet.getCell(dependsOnId);
            cell.removeAffectsOn(dependsOnId);
            dependsOn.clear();
        }
    }

    private void removeAffectsOn(String dependsOnId) {
        affectsOn.remove(dependsOnId);
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
        removeDependsOn();
        calculateEffectiveValue();
        detectCircularDependency(new HashSet<>());
        for (String affectedId : affectsOn) {
            CellImpl affectedCell = currSpreadSheet.getCell(affectedId);
            affectedCell.calculateEffectiveValue();
        }
    }

    private void detectCircularDependency(Set<String> visitedCells) {
        if (visitedCells.contains(this.id)) {
            throw new IllegalArgumentException("Circular dependency detected involving cell: " + this.id);
        }
        visitedCells.add(this.id);
        for (String dependencyId : dependsOn) {
            CellImpl dependentCell = currSpreadSheet.getCell(dependencyId);
            dependentCell.detectCircularDependency(new HashSet<>(visitedCells));
        }
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public void updateLastChangeAt(int currVersion) {
        lastChangeAt = currVersion++;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public int getLastChangeAt() {
        return lastChangeAt;
    }

    public Set<String> getDependsOn() {
        return dependsOn;
    }

    public void addAffectsOnId(String id) {
        affectsOn.add(id);
    }

    public Set<String> getAffectsOn() {
        return affectsOn;
    }

    public String getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public String getCol() {
        return col;
    }
}
