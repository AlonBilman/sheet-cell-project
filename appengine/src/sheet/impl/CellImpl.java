package sheet.impl;
import FileCheck.STLCell;
import expression.api.Expression;
import expression.api.*;
import expression.impl.*;
import expression.impl.Number;
import expression.impl.function.ConcatFunction;
import expression.impl.function.PlusFunction;
import sheet.api.EffectiveValue;

import java.util.ArrayList;
import java.util.List;

public class CellImpl {
    private final int row;
    private final String col;
    private final String id;
    private int lastChangeAt;
    private List<CellImpl> dependsOn;
    private List<CellImpl> affectsOn;
    private String originalValue;
    private EffectiveValue effectiveValue;


    public CellImpl(STLCell cell) {
        lastChangeAt = 0;
        this.row = cell.getRow();
        this.col = cell.getColumn();
        this.originalValue = cell.getSTLOriginalValue();
        this.id = generateId(col,row);
        dependsOn = new ArrayList<>();
        affectsOn = new ArrayList<>();
    }
    //maybe I get a string? and then edit the cell? {Bla Bla}?

    public void editCell(Expression value, int version, CellImpl... depends) {
        //originalValue = value;
      //  effectiveValue = value.eval().toString();
        updateLastChangeAt(version);
        updateCellsThatIAffect(); //maham
        updateCellsThatIDependsOn(depends);
    }

    private void updateCellsThatIDependsOn(CellImpl... depends) {
        //.... update the list. get rid of older that I don't depend on anymore
    }

    public void updateCellsThatIAffect() {
        //
    }

    public void calculateEffectiveValue() {
        //check if the original value is an expression or needs parsing
        if (originalValue != null && !originalValue.isEmpty()) {
            Expression expression = parseExpression(originalValue);
            //set the effective value of the cell
            this.effectiveValue = expression.eval();
        } else {
            //handle the case where there is no valid expression
            this.effectiveValue = null;
        }
    }

    private Expression parseExpression(String originalValue) {
        originalValue = originalValue.trim(); // Clean up the input

        if (originalValue.startsWith("{") && originalValue.endsWith("}")) {
            originalValue = originalValue.substring(1, originalValue.length() - 1);

            int firstCommaIndex = originalValue.indexOf(',');
            if (firstCommaIndex == -1) {
                throw new IllegalArgumentException("Invalid expression format: " + originalValue);
            }

            String functionName = originalValue.substring(0, firstCommaIndex).trim();
            //extract the arguments
            String arguments = originalValue.substring(firstCommaIndex + 1).trim();

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
                    //NEED TO ADD MORE FUNCTIONS OBV
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
                // End of an argument
                result.add(parseExpression(currentArgument.toString().trim()));
                currentArgument.setLength(0);
            } else {
                currentArgument.append(ch);
            }
        }
        if (currentArgument.length() > 0) {
            result.add(parseExpression(currentArgument.toString().trim()));
        }
        return result;
    }

    private Expression parseSimpleValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {

            String stringValue = value.substring(1, value.length() - 1);
            return new Mystring(stringValue);
        } else {
            try {
                return new Number(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unsupported value : " + value + " \n You may have forgotten to delineate the expression with {}.." +
                        " Or maybe you wanted a String so make sure to write it like this : \"<data>\"");
            }
        }

    }

     private String generateId(String col, int row) {
        char letter = col.charAt(0);
        return letter+String.valueOf(row+1);
    }


    Boolean IdChecker(String id){
        return id.equals(this.id);
    }

    public void removeDependsOn(CellImpl cellImpl) {
        dependsOn.remove(cellImpl);
    }

    public void updateLastChangeAt(int currVersion) {
        lastChangeAt = currVersion++;
    }

    public  String getOriginalValue() {
        return originalValue;
    }

    public int getLastChangeAt() {
        return lastChangeAt;
    }

    public List<CellImpl> getDependsOn() {
        return dependsOn;
    }

    public List<CellImpl> getAffectsOn() {
        return affectsOn;
    }

    public String getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // Update the CellImpl class to include this method
    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }
}
