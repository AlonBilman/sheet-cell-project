package sheet.impl;

import java.util.Set;

public class TestCellDependencies {
    public static void main(String[] args) {
        try {
            // Create a spreadsheet with a capacity of 10 rows and 10 columns
            SpreadSheetImpl sheet = new SpreadSheetImpl("TestSheet", 10, 10, 10, 10);

            // Add some cells
            CellImpl cellA1 = new CellImpl(1, "A");
            CellImpl cellB1 = new CellImpl(1, "B");
            CellImpl cellC1 = new CellImpl(1, "C");
            CellImpl cellD1 = new CellImpl(1, "D");
            CellImpl cellE1 = new CellImpl(1, "E");
            CellImpl cellF1 = new CellImpl(1, "F");
            CellImpl cellG1 = new CellImpl(1, "G");
            CellImpl cellH1 = new CellImpl(1, "H");
            CellImpl cellI1 = new CellImpl(1, "I");
            CellImpl cellJ1 = new CellImpl(1, "J");

            // Add cells to the spreadsheet
            sheet.addCell("A1", cellA1);
            sheet.addCell("B1", cellB1);
            sheet.addCell("C1", cellC1);
            sheet.addCell("D1", cellD1);
            sheet.addCell("E1", cellE1);
            sheet.addCell("F1", cellF1);
            sheet.addCell("G1", cellG1);
            sheet.addCell("H1", cellH1);
            sheet.addCell("I1", cellI1);
            sheet.addCell("J1", cellJ1);

            // Test 1: Simple addition
            System.out.println("Test 1: Simple addition (A1 = 10 + 20)");
            setAndCalculate(cellA1, "{PLUS,10,20}");
            printEffectiveValue(cellA1, "A1");

            // Test 2: Simple reference (B1 = A1)
            System.out.println("\nTest 2: Simple reference (B1 = A1)");
            setAndCalculate(cellB1, "{REF,A1}");
            printEffectiveValue(cellB1, "B1");

            // Test 3: Update A1, B1 should be updated (A1 = 30 + 10)
            System.out.println("\nTest 3: Update A1, B1 should be updated (A1 = 30 + 10)");
            cellA1.editCell("{PLUS,30,10}");
            printEffectiveValue(cellA1, "A1");
            printEffectiveValue(cellB1, "B1");

            // Test 4: More complex dependencies (C1 = A1 + B1)
            System.out.println("\nTest 4: More complex dependencies (C1 = A1 + B1)");
            setAndCalculate(cellC1, "{PLUS,{REF,A1},{REF,B1}}");
            printEffectiveValue(cellC1, "C1");

            // Test 5: Update A1 again, C1 should be updated (A1 = 40)
            System.out.println("\nTest 5: Update A1 again, C1 should be updated (A1 = 40)");
            cellA1.editCell("90");
            printEffectiveValue(cellA1, "A1");
            printEffectiveValue(cellC1, "C1");

            // Test 6: Circular reference error (D1 = E1, E1 = D1)
            System.out.println("\nTest 6: Circular reference error (D1 = E1, E1 = D1)");
            try {
                cellD1.editCell("{REF,E1}");
                cellE1.editCell("{REF,D1}");
                System.out.println("Circular reference set without immediate error.");
            } catch (Exception e) {
                System.out.println("Error during circular reference setup: " + e.getMessage());
            }

            // Test 7: Complex function chain (F1 = G1 + H1 + I1)
            System.out.println("\nTest 7: Complex function chain (F1 = G1 + H1 + I1)");
            setAndCalculate(cellF1, "{PLUS,{PLUS,{REF,G1},{REF,H1}},{REF,I1}}");
            printEffectiveValue(cellF1, "F1");

            // Test 8: Update G1 and H1, F1 should be updated
            System.out.println("\nTest 8: Update G1 and H1, F1 should be updated (G1 = 50, H1 = 60)");
            cellG1.editCell("50");
            cellH1.editCell("60");
            printEffectiveValue(cellG1, "G1");
            printEffectiveValue(cellH1, "H1");
            printEffectiveValue(cellF1, "F1");

            // Test 9: Deep dependency chain (J1 = F1 + A1 + C1)
            System.out.println("\nTest 9: Deep dependency chain (J1 = F1 + A1 + C1)");
            setAndCalculate(cellJ1, "{PLUS,{PLUS,{REF,F1},{REF,A1}},{REF,C1}}");
            printEffectiveValue(cellJ1, "J1");

            // Test 10: Update I1, J1 should be updated
            System.out.println("\nTest 10: Update I1, J1 should be updated (I1 = 70)");
            cellI1.editCell("70");
            printEffectiveValue(cellI1, "I1");
            printEffectiveValue(cellJ1, "J1");

            // Test 11: String concatenation chain (G1 = 'Hello ' + I1)
            System.out.println("\nTest 11: String concatenation chain (G1 = 'Hello ' + I1)");
            cellG1.editCell("{CONCAT,Hello ,{REF,I1}}");
            printEffectiveValue(cellG1, "G1");

            // Test 12: Update I1 again, G1 should reflect new I1 (I1 = 'World')")
            System.out.println("\nTest 12: Update I1 again, G1 should reflect new I1 (I1 = 'World')");
            cellI1.editCell("World");
            printEffectiveValue(cellI1, "I1");
            printEffectiveValue(cellG1, "G1");

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void setAndCalculate( CellImpl cell, String value) {
        try {
            cell.setOriginalValue(value);
            cell.calculateEffectiveValue();
        } catch (Exception e) {
            System.out.println("Error setting or calculating value: " + e.getMessage());
        }
    }

    private static void printEffectiveValue(CellImpl cell, String cellName) {
        try {
            System.out.println(cellName + " Effective Value: " + cell.getEffectiveValue().getValue());
            Set<String> dependsOn = cell.getDependsOn();
            System.out.print("Depends on: ");
            System.out.println(dependsOn);
            Set<String> affects = cell.getAffectsOn();
            System.out.print("Affects on: ");
            System.out.println(affects);
        } catch (Exception e) {
            System.out.println("Error getting " + cellName + "'s effective value: " + e.getMessage());
        }
    }
}
