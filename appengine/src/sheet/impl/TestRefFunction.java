package sheet.impl;

import java.util.Set;

public class TestRefFunction {
    public static void main(String[] args) {
        try {
            // Create a simple spreadsheet with a capacity of 10 rows and 10 columns
            SpreadSheetImpl sheet = new SpreadSheetImpl("TestSheet", 20, 20, 20, 20);

            // Add some cells
            CellImpl cellA1 = new CellImpl(1, "A", sheet);
            CellImpl cellB1 = new CellImpl(1, "B", sheet);
            CellImpl cellC1 = new CellImpl(1, "C", sheet);
            CellImpl cellD1 = new CellImpl(1, "D", sheet);
            CellImpl cellE1 = new CellImpl(1, "E", sheet);
            CellImpl cellF1 = new CellImpl(1, "F", sheet);
            CellImpl cellG1 = new CellImpl(1, "G", sheet);
            CellImpl cellH1 = new CellImpl(1, "H", sheet);
            CellImpl cellI1 = new CellImpl(1, "I", sheet);
            CellImpl cellJ1 = new CellImpl(1, "J", sheet);

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
            System.out.println("Test 1: Simple addition (A1 = 50 + 6)");
            setAndCalculate(sheet, cellA1, "{PLUS,50,6}");
            printEffectiveValue(cellA1, "A1");

            // Test 2: String concatenation
            System.out.println("\nTest 2: String concatenation (B1 = 'Hello' + 'World')");
            setAndCalculate(sheet, cellB1, "{CONCAT,Hello,World}");
            printEffectiveValue(cellB1, "B1");

            // Test 3: Reference to another cell
            System.out.println("\nTest 3: Reference to another cell (C1 = A1)");
            setAndCalculate(sheet, cellC1, "{REF,A1}");
            printEffectiveValue(cellC1, "C1");

            // Test 4: Nested concatenation with reference
            System.out.println("\nTest 4: Nested concatenation with reference (D1 = 'a' + B1 + 'blalba')");
            setAndCalculate(sheet, cellD1, "{CONCAT,{CONCAT,a,{REF,B1}},blalba}");
            printEffectiveValue(cellD1, "D1");

            // Test 5: Addition with references
            System.out.println("\nTest 5: Addition with references (E1 = A1 + C1)");
            setAndCalculate(sheet, cellE1, "{PLUS,{REF,A1},{REF,C1}}");
            printEffectiveValue(cellE1, "E1");

            // Test 6: Circular reference setup
            System.out.println("\nTest 6: Circular reference (F1 = G1, G1 = F1)");
            try {
                cellF1.setOriginalValue("{REF,G1}", sheet);
                cellG1.setOriginalValue("{REF,F1}", sheet);
                System.out.println("Circular reference set without immediate error.");
            } catch (Exception e) {
                System.out.println("Error during circular reference setup: " + e.getMessage());
            }

            // Test 7: Dependency chain (H1 = I1 + J1, I1 = 5, J1 = 10)
            System.out.println("\nTest 7: Dependency chain (H1 = I1 + J1)");
            setAndCalculate(sheet, cellI1, "{PLUS,0,5}");
            setAndCalculate(sheet, cellJ1, "{PLUS,0,10}");
            setAndCalculate(sheet, cellH1, "{PLUS,{REF,I1},{REF,J1}}");
            printEffectiveValue(cellH1, "H1");

            // Test 8: Circular dependency via intermediate cell (K1 = L1, L1 = M1, M1 = K1)
            System.out.println("\nTest 8: Circular dependency via intermediate cell");
            CellImpl cellK1 = new CellImpl(1, "K", sheet);
            CellImpl cellL1 = new CellImpl(1, "L", sheet);
            CellImpl cellM1 = new CellImpl(1, "M", sheet);
            sheet.addCell("K1", cellK1);
            sheet.addCell("L1", cellL1);
            sheet.addCell("M1", cellM1);
            try {
                cellK1.setOriginalValue("{REF,L1}", sheet);
                cellL1.setOriginalValue("{REF,M1}", sheet);
                cellM1.setOriginalValue("{REF,K1}", sheet);
                System.out.println("Circular reference via intermediate cells set without immediate error.");
            } catch (Exception e) {
                System.out.println("Error during circular reference setup: " + e.getMessage());
            }
            calculateAndPrintWithCatch(sheet, cellK1, "K1");
            calculateAndPrintWithCatch(sheet, cellL1, "L1");
            calculateAndPrintWithCatch(sheet, cellM1, "M1");

            // Test 9: Deeply nested references (N1 = O1, O1 = P1, P1 = Q1, Q1 = 100)
            System.out.println("\nTest 9: Deeply nested references");
            CellImpl cellN1 = new CellImpl(1, "N", sheet);
            CellImpl cellO1 = new CellImpl(1, "O", sheet);
            CellImpl cellP1 = new CellImpl(1, "P", sheet);
            CellImpl cellQ1 = new CellImpl(1, "Q", sheet);
            sheet.addCell("N1", cellN1);
            sheet.addCell("O1", cellO1);
            sheet.addCell("P1", cellP1);
            sheet.addCell("Q1", cellQ1);
            setAndCalculate(sheet, cellQ1, "{PLUS,0,100}");
            setAndCalculate(sheet, cellP1, "{REF,Q1}");
            setAndCalculate(sheet, cellO1, "{REF,P1}");
            setAndCalculate(sheet, cellN1, "{REF,O1}");
            printEffectiveValue(cellN1, "N1");

            // Test 10: Complex formula (R1 = (S1 + T1) * (U1 - V1))
            System.out.println("\nTest 10: Complex formula (R1 = (S1 + T1) * (U1 - V1))");
            CellImpl cellR1 = new CellImpl(1, "R", sheet);
            CellImpl cellS1 = new CellImpl(1, "S", sheet);
            CellImpl cellT1 = new CellImpl(1, "T", sheet);
            CellImpl cellU1 = new CellImpl(1, "U", sheet);
            CellImpl cellV1 = new CellImpl(1, "V", sheet);
            sheet.addCell("R1", cellR1);
            sheet.addCell("S1", cellS1);
            sheet.addCell("T1", cellT1);
            sheet.addCell("U1", cellU1);
            sheet.addCell("V1", cellV1);
            setAndCalculate(sheet, cellS1, "{PLUS,0,10}");
            setAndCalculate(sheet, cellT1, "{PLUS,0,5}");
            setAndCalculate(sheet, cellU1, "{PLUS,0,15}");
            setAndCalculate(sheet, cellV1, "{PLUS,0,3}");
            setAndCalculate(sheet, cellR1, "{PLUS,{PLUS,{REF,S1},{REF,T1}},{MINUS,{REF,U1},{REF,V1}}}");
            printEffectiveValue(cellR1, "R1");

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void setAndCalculate(SpreadSheetImpl sheet, CellImpl cell, String value) {
        try {
            cell.setOriginalValue(value, sheet);
            cell.calculateEffectiveValue(sheet);
        } catch (Exception e) {
            System.out.println("Error setting or calculating value: " + e.getMessage());
        }
    }

    private static void calculateAndPrintWithCatch(SpreadSheetImpl sheet, CellImpl cell, String cellName) {
        try {
            cell.calculateEffectiveValue(sheet);
            printEffectiveValue(cell, cellName);
        } catch (ArithmeticException e) {
            System.out.println(cellName + ": Circular dependency detected: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error calculating " + cellName + "'s effective value: " + e.getMessage());
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
