package sheet.impl;

import java.util.Set;

public class TestCellDependencies {
    public static void main(String[] args) {
        try {
            // Create a spreadsheet with a capacity of 20 rows and 20 columns
            SpreadSheetImpl sheet = new SpreadSheetImpl("TestSheet", 20, 20, 20, 20);

            // Add some cells
            CellImpl cellA1 = createAndAddCell(sheet, "A1");
            CellImpl cellB1 = createAndAddCell(sheet, "B1");
            CellImpl cellC1 = createAndAddCell(sheet, "C1");
            CellImpl cellD1 = createAndAddCell(sheet, "D1");
            CellImpl cellE1 = createAndAddCell(sheet, "E1");
            CellImpl cellF1 = createAndAddCell(sheet, "F1");
            CellImpl cellG1 = createAndAddCell(sheet, "G1");
            CellImpl cellH1 = createAndAddCell(sheet, "H1");
            CellImpl cellI1 = createAndAddCell(sheet, "I1");
            CellImpl cellJ1 = createAndAddCell(sheet, "J1");

            // Test 1: Simple addition
            runTest(() -> {
                System.out.println("Test 1: Simple addition (A1 = 10 + 20)");
                setAndCalculate(cellA1, "{PLUS,10,20}");
                printEffectiveValue(cellA1, "A1");
            });

            // Test 2: Simple reference (B1 = A1)
            runTest(() -> {
                System.out.println("\nTest 2: Simple reference (B1 = A1)");
                setAndCalculate(cellB1, "{REF,A1}");
                printEffectiveValue(cellB1, "B1");
            });

            // Test 3: Update A1, B1 should be updated (A1 = 30 + 10)
            runTest(() -> {
                System.out.println("\nTest 3: Update A1, B1 should be updated (A1 = 30 + 10)");
                cellA1.editCell("{PLUS,30,10}");
                printEffectiveValue(cellA1, "A1");
                printEffectiveValue(cellB1, "B1");
            });

            // Test 4: More complex dependencies (C1 = A1 + B1)
            runTest(() -> {
                System.out.println("\nTest 4: More complex dependencies (C1 = A1 + B1)");
                setAndCalculate(cellC1, "{PLUS,{REF,A1},{REF,B1}}");
                printEffectiveValue(cellC1, "C1");
            });

            // Test 5: Update A1 again, C1 should be updated (A1 = 40)
            runTest(() -> {
                System.out.println("\nTest 5: Update A1 again, C1 should be updated (A1 = 40)");
                cellA1.editCell("90");
                printEffectiveValue(cellA1, "A1");
                printEffectiveValue(cellC1, "C1");
            });

            // Test 6: Circular reference error (D1 = E1, E1 = D1)
            runTest(() -> {
                System.out.println("\nTest 6: Circular reference error (D1 = E1, E1 = D1)");
                try {
                    cellD1.editCell("{REF,E1}");
                    cellE1.editCell("{REF,D1}");
                    System.out.println("Circular reference set without immediate error.");
                } catch (Exception e) {
                    System.out.println("Error during circular reference setup: " + e.getMessage());
                }
            });

            // Test 7: Complex function chain (F1 = G1 + H1 + I1)
            runTest(() -> {
                System.out.println("\nTest 7: Complex function chain (F1 = G1 + H1 + I1)");
                setAndCalculate(cellF1, "{PLUS,{PLUS,{REF,G1},{REF,H1}},2}");
                printEffectiveValue(cellF1, "F1");
            });

            // Test 8: Update G1 and H1, F1 should be updated
            runTest(() -> {
                System.out.println("\nTest 8: Update G1 and H1, F1 should be updated (G1 = 50, H1 = 60)");
                cellG1.editCell("50");
                cellH1.editCell("60");
                printEffectiveValue(cellG1, "G1");
                printEffectiveValue(cellH1, "H1");
                printEffectiveValue(cellF1, "F1");
            });

            // Test 9: Deep dependency chain (J1 = F1 + A1 + C1)
            runTest(() -> {
                System.out.println("\nTest 9: Deep dependency chain (J1 = F1 + A1 + C1)");
                setAndCalculate(cellJ1, "{PLUS,{PLUS,{REF,F1},{REF,A1}},{REF,C1}}");
                printEffectiveValue(cellJ1, "J1");
            });

            // Test 10: Update I1, J1 should be updated
            runTest(() -> {
                System.out.println("\nTest 10: Update I1, J1 should be updated (I1 = 70)");
                cellI1.editCell("70");
                printEffectiveValue(cellI1, "I1");
                printEffectiveValue(cellJ1, "J1");
            });

            // Test 11: String concatenation chain (G1 = 'Hello ' + I1)
            runTest(() -> {
                System.out.println("\nTest 11: String concatenation chain (G1 = 'Hello ' + I1)");
                cellG1.editCell("{CONCAT,Hello ,{REF,I1}}");
                printEffectiveValue(cellG1, "G1");
            });

            // Test 12: Update I1 again, G1 should reflect new I1 (I1 = 'World')")
            runTest(() -> {
                System.out.println("\nTest 12: Update I1 again, G1 should reflect new I1 (I1 = 'World')");
                cellI1.editCell("World");
                printEffectiveValue(cellI1, "I1");
                printEffectiveValue(cellG1, "G1");
            });

        } catch (Exception e) {
            System.out.println("An unexpected error occurred in the main test execution: " + e.getMessage());
        }
    }

    private static CellImpl createAndAddCell(SpreadSheetImpl sheet, String cellId) {
        try {
            CellImpl cell = new CellImpl(1, cellId);
            sheet.addCell(cellId, cell);
            return cell;
        } catch (Exception e) {
            System.out.println("Error creating or adding cell " + cellId + ": " + e.getMessage());
            return null;
        }
    }

    private static void setAndCalculate(CellImpl cell, String value) {
        try {
            cell.setOriginalValue(value);
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

    private static void runTest(Runnable test) {
        try {
            test.run();
        } catch (Exception e) {
            System.out.println("An error occurred during the test: " + e.getMessage());
        }
    }
}
