package sheet.impl;

import FileCheck.STLCell;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.Mystring;
import expression.impl.Number;
import expression.impl.function.*;
import expression.impl.function.AbsFunction;
import sheet.api.EffectiveValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellImpl implements Serializable {
    private final int row;
    private final String col;
    private final String id;
    private int lastChangeAt;
    private Set<String> dependsOn;
    private Set<String> affectsOn;
    private String originalValue;
    private EffectiveValue effectiveValue;

    private static SpreadSheetImpl currSpreadSheet;

    public CellImpl(int row, String col, String newOriginalVal, int versionNumber) {
        this.row = row;
        this.col = col;
        this.id = generateId(col, row);
        dependsOn = new HashSet<>();
        affectsOn = new HashSet<>();
        setOriginalValue(newOriginalVal);
        lastChangeAt = ++versionNumber;
    }

    private void checkRowAndCol(int row, String col) {
        if (!(col != null && col.length() == 1 && Character.isLetter(col.charAt(0)))) {
            throw new IllegalArgumentException("One or more of the Cells have invalid id. found: \"" + col + "\" as col.\n" +
                    "Cell's ID must contain a letter followed by a number. Meaning column has to be a letter.");
        }
        int colInt = Character.getNumericValue(col.charAt(0)) - Character.getNumericValue('A'); //getting the col

        if (colInt < 0 || row < 0 || row >= currSpreadSheet.getRowSize() || colInt >= currSpreadSheet.getColumnSize()) {
            throw new IllegalArgumentException("One or more cells are out of sheet boundaries. Found \"" + col + "\" as column and \"" + row + "\" as row.");
        }
        //everything is fine. well in that case...return..
    }

    public CellImpl(STLCell cell) {
        lastChangeAt = 1;
        this.row = cell.getRow();
        this.col = cell.getColumn();
        checkRowAndCol(row, col);
        this.id = generateId(col, row);
        dependsOn = new HashSet<>();
        affectsOn = new HashSet<>();
        setOriginalValue(cell.getSTLOriginalValue());
    }

    public static void setSpreadSheet(SpreadSheetImpl spreadSheet) {
        currSpreadSheet = spreadSheet;
    }

    public void calculateEffectiveValue() {
        if (originalValue != null) {
            if (!originalValue.isEmpty()) {
                Expression expression = parseExpression(originalValue);
                this.effectiveValue = expression.eval();
            } else this.effectiveValue = new EffectiveValueImpl("", ObjType.STRING);
        } else {
            //handle the case where there is no valid expression
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

                case "ABS":
                    if (parsedArguments.size() != 1) {
                        throw new IllegalArgumentException("ABS function requires one arguments.");
                    }
                    return new AbsFunction(parsedArguments.get(0));

                case "POW":
                    if (parsedArguments.size() != 2) {
                        throw new IllegalArgumentException("POW function requires two arguments.");
                    }
                    return new PowFunction(parsedArguments.get(0),parsedArguments.get(1));

                case "MOD":
                    if (parsedArguments.size() != 2) {
                        throw new IllegalArgumentException("MOD function requires two arguments.");
                    }
                    return new ModFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "MINUS":
                    if (parsedArguments.size() != 2) {
                        throw new IllegalArgumentException("MINUS function requires two arguments.");
                    }
                    return new MinusFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "CONCAT":
                    if (parsedArguments.size() != 2) {
                        throw new IllegalArgumentException("CONCAT function requires two arguments.");
                    }
                    return new ConcatFunction(parsedArguments.get(0), parsedArguments.get(1));
                //NEED TO ADD MORE FUNCTIONS OBV

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
            if (value.startsWith("-")) {
                try {
                    return new Number(Double.valueOf(value));
                } catch (NumberFormatException ignored) {
                }
            } else return new Mystring(value);

        return new Number(Double.valueOf(value));
    }

    private String generateId(String col, int row) {
        char letter = col.charAt(0);
        return letter + String.valueOf(row);
    }

    private void removeDependsOn() {
        for (String dependsOnId : dependsOn) {
            CellImpl cell = currSpreadSheet.getCell(dependsOnId);
            cell.removeAffectsOn(this.id);
        }
        dependsOn.clear();
    }

    private void removeAffectsOn(String id) {
        affectsOn.remove(id);
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
        updateLastChangeAt(currSpreadSheet.getSheetVersionNumber());
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
        lastChangeAt = ++currVersion;
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
