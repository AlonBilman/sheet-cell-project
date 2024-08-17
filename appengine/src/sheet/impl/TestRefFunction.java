package sheet.impl;

import java.util.Set;

public class TestRefFunction {
    public static void main(String[] args) {
        try {
            // Create a simple spreadsheet with 3 rows and 3 columns
            SpreadSheetImpl sheet = new SpreadSheetImpl("hello", 10, 10, 10, 10);

            // Add some cells
            CellImpl cellA1 = new CellImpl(1, "A");
            CellImpl cellB1 = new CellImpl(1, "B");
            CellImpl cellC1 = new CellImpl(1, "C");
            CellImpl cellD1 = new CellImpl(1, "D");
            CellImpl cellE1 = new CellImpl(1, "E");
            CellImpl cellF1 = new CellImpl(1, "F");
            CellImpl cellG1 = new CellImpl(1, "G");

            sheet.addCell("A1", cellA1);
            sheet.addCell("B1", cellB1);
            sheet.addCell("C1", cellC1);
            sheet.addCell("D1", cellD1);
            sheet.addCell("E1", cellE1);
            sheet.addCell("F1", cellF1);
            sheet.addCell("G1", cellG1);

            try {
                // Set values
                cellA1.setOriginalValue("{PLUS,50,6}", sheet);                      // A1 = 50 + 6 = 56
                cellB1.setOriginalValue("{CONCAT,\"Hello\",\"World\"}", sheet);     // B1 = "Hello" + "World" = "HelloWorld"
                cellC1.setOriginalValue("{REF,\"A1\"}", sheet);                     // C1 = A1 = 56
                cellD1.setOriginalValue("{CONCAT,{REF,\"B1\"},\"123\"}", sheet);    // D1 = B1 + "123" = "HelloWorld123"
                cellE1.setOriginalValue("{PLUS,{REF,\"A1\"},{REF,\"C1\"}}", sheet); // E1 = A1 + C1 = 56 + 56 = 112
                cellG1.setOriginalValue("{PLUS,{REF,\"A1\"},20}", sheet);           // G1 = A1 + 20 = 76

            } catch (Exception e) {
                System.out.println("Error setting cell values: " + e.getMessage());
            }

            // Calculate effective values
            calculateAndPrintValue(sheet, cellA1, "A1"); // 56
            calculateAndPrintValue(sheet, cellB1, "B1"); // HelloWorld
            calculateAndPrintValue(sheet, cellC1, "C1"); // 56
            calculateAndPrintValue(sheet, cellD1, "D1"); // HelloWorld123
            calculateAndPrintValue(sheet, cellE1, "E1"); // 112
            calculateAndPrintValue(sheet, cellG1, "G1"); // 76

            // Try to calculate F1's value, expecting an error
            try {
                cellF1.calculateEffectiveValue(sheet);
            } catch (ArithmeticException e) {
                System.out.println("Error in F1: " + e.getMessage());
            }

            // Output the results
            printEffectiveValue(cellA1, "A1"); // 56
            printEffectiveValue(cellB1, "B1"); // HelloWorld
            printEffectiveValue(cellC1, "C1"); // 56
            printEffectiveValue(cellD1, "D1"); // HelloWorld123
            printEffectiveValue(cellE1, "E1"); // 112
            printEffectiveValue(cellG1, "G1"); // 76

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void calculateAndPrintValue(SpreadSheetImpl sheet, CellImpl cell, String cellName) {
        try {
            cell.calculateEffectiveValue(sheet);
        } catch (Exception e) {
            System.out.println("Error calculating " + cellName + "'s effective value: " + e.getMessage());
        }
    }

    private static void printEffectiveValue(CellImpl cell, String cellName) {
        try {
            System.out.println(cellName + " Effective Value: " + cell.getEffectiveValue().getValue());
            Set<String> dependsOn= cell.getDependsOn();
            System.out.print("Depends on: ") ;
            System.out.println(dependsOn);
            Set<String> affects= cell.getAffectsOn();
            System.out.print("Affects on:") ;
            System.out.println(affects);

        } catch (Exception e) {
            System.out.println("Error getting " + cellName + "'s effective value: " + e.getMessage());
        }
    }
}