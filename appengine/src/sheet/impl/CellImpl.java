package sheet.impl;

import checkfile.STLCell;
import expression.api.ErrorValues;
import expression.api.Expression;
import expression.api.ObjType;
import expression.impl.simple.expression.Bool;
import expression.impl.simple.expression.expString;
import expression.impl.simple.expression.Number;
import expression.impl.function.*;
import expression.impl.function.AbsFunction;
import sheet.api.EffectiveValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class CellImpl implements Serializable {
    private int row;
    private String col;
    private String id;
    private int lastChangeAt;
    private Set<String> dependsOn;
    private Set<String> affectsOn;
    private Set<Range> dependsOnRange;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private CellColor cellColor;
    private static SpreadSheetImpl currSpreadSheet;

    public CellImpl(int row, String col, String newOriginalVal, int versionNumber) {
        this.row = row;
        this.col = col;
        this.id = generateId(col, row);
        cellColor = new CellColor(null, null);
        dependsOn = new HashSet<>();
        affectsOn = new HashSet<>();
        dependsOnRange = new HashSet<>();
        setOriginalValue(newOriginalVal, true);
        if (newOriginalVal == null)
            lastChangeAt = 1;
        else lastChangeAt = ++versionNumber;
    }


    private void checkRowAndCol(int row, String col) {
        if (!(col != null && col.length() == 1 && Character.isLetter(col.charAt(0)))) {
            throw new IllegalArgumentException("One or more of the Cells have invalid id. found: \"" + col + "\" as col.\n" +
                    "Cell's ID must contain a letter followed by a number. Meaning column has to be a letter.");
        }
        int colInt = Character.getNumericValue(col.charAt(0)) - Character.getNumericValue('A'); //getting the col

        if (colInt < 0 || row <= 0 || row > currSpreadSheet.getRowSize() || colInt > currSpreadSheet.getColumnSize() - 1) {
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
        dependsOnRange = new HashSet<>();
        cellColor = new CellColor(null, null);
        setOriginalValue(cell.getSTLOriginalValue(), true);
    }

    public static void setSpreadSheet(SpreadSheetImpl spreadSheet) {
        currSpreadSheet = spreadSheet;
    }

    public void calculateEffectiveValue() {
        if (originalValue == null || originalValue.isEmpty()) {
            originalValue = "Empty Cell";
            this.effectiveValue = new EffectiveValueImpl(ErrorValues.EMPTY.getErrorMessage(), ObjType.EMPTY);
        } else {
            Expression expression = parseExpression(originalValue);
            try {
                this.effectiveValue = expression.eval();
            } catch (Exception e) {
                throw new IllegalArgumentException("Involving Cell " + id + "\n" + e.getMessage());
            }
        }
        detectCircularDependency(new HashSet<>());
        //recursive like dps algo aka - "Maham". -> dfs with circle detection
        for (String affectId : affectsOn) {
            CellImpl dep = currSpreadSheet.getCell(affectId);
            dep.calculateEffectiveValue();
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
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("PLUS function requires two arguments.");
                    return new PlusFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "ABS":
                    if (parsedArguments.size() != 1)
                        throw new IllegalArgumentException("ABS function requires one arguments.");
                    return new AbsFunction(parsedArguments.get(0));

                case "SUB":
                    if (parsedArguments.size() != 3)
                        throw new IllegalArgumentException("SUB function requires three arguments.");
                    return new SubFunction(parsedArguments.get(0), parsedArguments.get(1), parsedArguments.get(2));

                case "TIMES":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("TIMES function requires two arguments.");
                    return new TimesFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "DIVIDE":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("DIVIDE function requires two arguments.");
                    return new DivideFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "POW":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("POW function requires two arguments.");
                    return new PowFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "MOD":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("MOD function requires two arguments.");
                    return new ModFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "MINUS":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("MINUS function requires two arguments.");
                    return new MinusFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "CONCAT":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("CONCAT function requires two arguments.");
                    return new ConcatFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "REF":
                    if (parsedArguments.size() != 1)
                        throw new IllegalArgumentException("REF function requires one argument.");
                    if (arguments.trim().startsWith("{")) {
                        throw new IllegalArgumentException("REF function does not allow functions! only cell's Id");
                    }
                    Expression argument = parsedArguments.get(0);
                    Expression res = new CellReferenceFunc(argument, currSpreadSheet, this.id);
                    String referencedId = argument.eval().getValue().toString().toUpperCase().trim();
                    dependsOn.add(referencedId);
                    return res;

                case "AVERAGE":
                    if (parsedArguments.size() != 1)
                        throw new IllegalArgumentException("AVERAGE function requires one argument.");
                    Expression name1 = parsedArguments.get(0);
                    Range range1 = avgFunctionCheck(name1);
                    Expression avgFunc = new AverageFunction(range1);
                    if (range1 != null) {
                        dependsOnRange.add(range1);
                        range1.addAffectsOnCells(this);
                    }
                    return avgFunc;

                case "SUM":
                    if (parsedArguments.size() != 1)
                        throw new IllegalArgumentException("SUM function requires one argument.");
                    Expression name2 = parsedArguments.get(0);
                    Range range2 = sumFuncCheck(name2);
                    Expression sumFunc = new SumFunction(range2);
                    if (range2 != null) {
                        dependsOnRange.add(range2);
                        range2.addAffectsOnCells(this);
                    }
                    return sumFunc;

                case "AND":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("AND function requires two arguments.");
                    return new AndFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "BIGGER":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("BIGGER function requires two arguments.");
                    return new BiggerFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "LESS":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("LESS function requires two arguments.");
                    return new LessFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "NOT":
                    if (parsedArguments.size() != 1)
                        throw new IllegalArgumentException("NOT function requires one argument.");
                    return new NotFunction(parsedArguments.get(0));

                case "OR":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("OR function requires two arguments.");
                    return new OrFunction(parsedArguments.get(0), parsedArguments.get(1));

                case "IF":
                    if (parsedArguments.size() != 3)
                        throw new IllegalArgumentException("IF function requires three arguments.");
                    return new ifCondition(parsedArguments.get(0), parsedArguments.get(1), parsedArguments.get(2));

                case "PERCENT":
                    if (parsedArguments.size() != 2)
                        throw new IllegalArgumentException("PERCENT function requires two arguments.");
                    return new PercentFunction(parsedArguments.get(0), parsedArguments.get(1));

                default:
                    throw new IllegalArgumentException("Unknown/Unsupported function: " + functionName);
            }
        } else {
            return parseSimpleValue(originalValue);
        }
    }

    private Range avgFunctionCheck(Expression name1) {
        return rangeFunctionCheck(name1, true);
    }

    private Range sumFuncCheck(Expression name2) {
        return rangeFunctionCheck(name2, false);
    }

    private Range rangeFunctionCheck(Expression name, Boolean isAverage) {
        EffectiveValue effectiveValue = name.eval();
        if (effectiveValue.getObjType() != ObjType.STRING) {
            if (isAverage)
                throw new IllegalArgumentException("AVERAGE function requires a string argument (Range name).");
            throw new IllegalArgumentException("SUM function requires a string argument (Range name).");
        }
        String arg = name.eval().getValue().toString(); //the name of the Range
        Range range = currSpreadSheet.getRange(arg);
        if (range != null) {
            for (CellImpl cell : range.getRangeCells()) {
                cell.addAffectsOnId(this.id);
                dependsOn.add(cell.getId());
            }
        }
        return range;
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
            result.add(parseExpression(currentArgument.toString()));
        }
        return result;
    }

    private Expression parseSimpleValue(String value) {
        if (value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("FALSE"))
            return new Bool(Boolean.valueOf(value));

        if (value.isEmpty() || value.matches(".*[^0-9].*") || value.matches("^\\s*$")) {
            if (value.startsWith("-") || value.contains(".")) {
                try {
                    return new Number(Double.valueOf(value));
                } catch (NumberFormatException ignored) {
                    return new expString(value);
                }
            } else return new expString(value);
        }
        return new Number(Double.valueOf(value));
    }

    private String generateId(String col, int row) {
        char letter = Character.toUpperCase(col.charAt(0));
        return letter + String.valueOf(row);
    }

    private void removeDependsOn() {
        for (String dependsOnId : dependsOn) {
            CellImpl cell = currSpreadSheet.getCell(dependsOnId);
            cell.removeAffectsOn(this.id);
        }
        dependsOn.clear();
        for (Range range : dependsOnRange) {
            range.removeAffectsOnCells(this);
        }
        dependsOnRange.clear();
    }

    private void removeAffectsOn(String id) {
        affectsOn.remove(id);
    }

    public void setOriginalValue(String originalValue, boolean editVersion) {
        this.originalValue = originalValue;
        removeDependsOn();
        calculateEffectiveValue();
        if (editVersion)
            updateLastChangeAt(currSpreadSheet.getSheetVersionNumber());
        if (originalValue != null) {
            this.originalValue = Pattern.compile("\\{REF,([^}]+)}").matcher(originalValue)
                    .replaceAll(match -> "{REF," + match.group(1).toUpperCase() + "}");
        }
    }

    private void detectCircularDependency(Set<String> visitedCells) {

        if (visitedCells.contains(this.id)) {
            throw new IllegalArgumentException("Circular dependency detected! Trace: " + this.id);
        }
        try {
            visitedCells.add(this.id);
            for (String dependencyId : dependsOn) {
                CellImpl dependentCell = currSpreadSheet.getCell(dependencyId);
                dependentCell.detectCircularDependency(new HashSet<>(visitedCells));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage() + " -> " + this.id);
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

    public void setRow(int row) {
        this.row = row;
        this.id = generateId(this.col, this.row);
    }

    public void setTextColor(String textColor) {
        cellColor.setTextColor(textColor);
    }

    public void setBackgroundColor(String backgroundColor) {
        cellColor.setBackgroundColor(backgroundColor);
    }

    public CellColor getCellColor() {
        return cellColor;
    }
}
