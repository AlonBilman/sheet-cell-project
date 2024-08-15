package expression.api.impl.function;

import expression.api.Expression;
import expression.api.impl.Number;
import expression.api.impl.function.testemp.MinusFunction;
import sheet.impl.Cell;
import sheet.impl.SpreadSheet;

public class MainToCheck {
    public static void main(String[] args) {
        // Create a 5x5 spreadsheet
        SpreadSheet sheet = new SpreadSheet(5, 5, "TestSheet");

        // Create an expression (assuming Expression is a functional interface with a method eval that returns a value)
        Expression expression = new MinusFunction(new Number(0.3), new Number(4));
        Expression expression2 = new MinusFunction(new Number(0.3), new Number(4));
        // Edit cell A1
        Cell cellA1 = sheet.getCell("A1");
        cellA1.editCell(expression, sheet.getSheetVersionNumber());

        // Print out the details of cell A1
        System.out.println("Cell ID: " + cellA1.getId());
        System.out.println("Original Value: " + cellA1.getOriginalValue().eval());
        System.out.println("Effective Value: " + cellA1.getOriginalValue().eval().toString());
        System.out.println("Last Change Version: " + cellA1.getLastChangeAt());

        Cell cellA2 = sheet.getCell("A1");
        cellA1.editCell(expression2, sheet.getSheetVersionNumber());

        // Retrieve the same cell using the SpreadSheet class and check if it matches
        Cell retrievedCell = sheet.getCell("A1");
        System.out.println("Retrieved Cell ID: " + retrievedCell.getId());
        System.out.println("Retrieved Original Value: " + retrievedCell.getOriginalValue().eval());
        System.out.println("Retrieved Effective Value: " + retrievedCell.getOriginalValue().eval().toString());
        System.out.println("Retrieved Last Change Version: " + retrievedCell.getLastChangeAt());

        // Assert to check if the cell has been updated correctly
        assert retrievedCell.getOriginalValue().eval().equals(42.0) : "Cell value mismatch!";
        assert retrievedCell.getLastChangeAt() == sheet.getSheetVersionNumber() : "Version number mismatch!";

        System.out.println("All tests passed successfully!");
    }
}


