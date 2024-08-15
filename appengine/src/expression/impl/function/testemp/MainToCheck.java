//package expression.impl.function;
//
//import expression.api.Expression;
//import expression.impl.Number;
//import expression.impl.function.testemp.MinusFunction;
//import sheet.impl.CellImpl;
//import sheet.impl.SpreadSheetImpl;
//
//public class MainToCheck {
//    public static void main(String[] args) {
//        // Create a 5x5 spreadsheet
//        SpreadSheetImpl sheet = new SpreadSheetImpl(5, 5, "TestSheet");
//
//        // Create an expression (assuming Expression is a functional interface with a method eval that returns a value)
//        Expression expression = new MinusFunction(new Number(0.3), new Number(4));
//        Expression expression2 = new MinusFunction(new Number(0.3), new Number(4));
//        // Edit cell A1
//        CellImpl cellA1 = sheet.getCell("A1");
//        cellA1.editCell(expression, sheet.getSheetVersionNumber());
//
//        // Print out the details of cell A1
//        System.out.println("CellImpl ID: " + cellA1.getId());
//        System.out.println("Original Value: " + cellA1.getOriginalValue().eval());
//        System.out.println("Effective Value: " + cellA1.getOriginalValue().eval().toString());
//        System.out.println("Last Change Version: " + cellA1.getLastChangeAt());
//
//        CellImpl cellA2 = sheet.getCell("A1");
//        cellA1.editCell(expression2, sheet.getSheetVersionNumber());
//
//        // Retrieve the same cell using the SpreadSheetImpl class and check if it matches
//        CellImpl retrievedCell = sheet.getCell("A1");
//        System.out.println("Retrieved CellImpl ID: " + retrievedCell.getId());
//        System.out.println("Retrieved Original Value: " + retrievedCell.getOriginalValue().eval());
//        System.out.println("Retrieved Effective Value: " + retrievedCell.getOriginalValue().eval().toString());
//        System.out.println("Retrieved Last Change Version: " + retrievedCell.getLastChangeAt());
//
//        // Assert to check if the cell has been updated correctly
//        assert retrievedCell.getOriginalValue().eval().equals(42.0) : "CellImpl value mismatch!";
//        assert retrievedCell.getLastChangeAt() == sheet.getSheetVersionNumber() : "Version number mismatch!";
//
//        System.out.println("All tests passed successfully!");
//    }
//}
//
//
