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
    private static SpreadSheetImpl lastUpdatedSpreadSheet;

    public CellImpl(int row, String col) {
        this.row = row;
        this.col = col;
        this.id = generateId(col, row);
        dependsOn = new HashSet<>();
        affectsOn = new HashSet<>();
    }

    public CellImpl(STLCell cell,SpreadSheetImpl spreadSheet) {
        lastChangeAt = 1;
        this.row = cell.getRow();
        this.col = cell.getColumn();
        dependsOn = new HashSet<>();
        affectsOn = new HashSet<>();
        lastUpdatedSpreadSheet = spreadSheet;
        setOriginalValue(cell.getSTLOriginalValue(),lastUpdatedSpreadSheet);
        this.id = generateId(col, row);

    }
    //maybe I get a string? and then edit the cell? {Bla Bla}?

    public void editCell(String value, int version, CellImpl... depends) {
        //originalValue = value;
        //  effectiveValue = value.eval().toString();
        //  updateLastChangeAt(version);
        // updateCellsThatIAffect(); //maham
        //  updateCellsThatIDependsOn(depends);
    }

    private void updateCellsThatIDependsOn(CellImpl... depends) {
        //.... update the list. get rid of older that I don't depend on anymore
    }

    public void updateCellsThatIAffect() {
        //
    }

    public void calculateEffectiveValue(SpreadSheetImpl currSheet) {
        //check if the original value is an expression or needs parsing
        if (originalValue != null && !originalValue.isEmpty()) {
            Expression expression = parseExpression(originalValue, currSheet);
            //set the effective value of the cell
            this.effectiveValue = expression.eval();
        } else {
            //handle the case where there is no valid expression
            this.effectiveValue = null;
        }
    }

    private Expression parseExpression(String originalValue, SpreadSheetImpl currSheet) {
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

            List<Expression> parsedArguments = parseArguments(arguments,currSheet);

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

                case "REF":
                    if (parsedArguments.size() != 1) {
                        throw new IllegalArgumentException("REF function requires one argument.");
                    }
                    Expression argument = parsedArguments.get(0);
                    Expression res = new CellReferenceFunc(argument, currSheet,this.id);
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

    private List<Expression> parseArguments(String arguments, SpreadSheetImpl currSheet) {
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
                result.add(parseExpression(currentArgument.toString().trim(), currSheet));
                currentArgument.setLength(0);
            } else {
                currentArgument.append(ch);
            }
        }
        if (!currentArgument.isEmpty()) {
            result.add(parseExpression(currentArgument.toString().trim(), currSheet));
        }
        return result;
    }

    private Expression parseSimpleValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            // Handle quoted strings
            String stringValue = value.substring(1, value.length() - 1);
            return new Mystring(stringValue);
        }
        // Try to parse as a number
        try {
            return new Number(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unsupported value: " + value +
                    "\nYou may have forgotten to delineate the expression with {}.." +
                    " Or maybe you wanted a String, so make sure to write it like this: \"<data>\" or (<data>)");
        }


    }
     private String generateId(String col, int row) {
        char letter = col.charAt(0);
        return letter+String.valueOf(row);
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

    public Set<String> getDependsOn() {
        return dependsOn;
    }
    public void addAffectsOnId(String id){
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

    // Update the CellImpl class to include this method
    public void setOriginalValue(String originalValue, SpreadSheetImpl currSheet) {
        this.originalValue = originalValue;
        calculateEffectiveValue(currSheet);
        //maham
    }


    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

}