package sheet.impl;

import java.util.Set;

public  class TestCellDependencies {
    public static void main(String[] args) {

            SpreadSheetImpl sheet = new SpreadSheetImpl("TestSheet", 20, 20, 20, 20);

            sheet.addCell(1,"A","5");
            sheet.addCell(2,"B","6");
            sheet.addCell(3,"C","7");
            sheet.addCell(4,"D","8");
            sheet.addCell(5,"E","9");
            // Test 1: Simple addition

                        System.out.println("Test 1: Simple addition (A1 = 10 + 20)");
                        sheet.changeCell("A1", "{PLUS,10,20}");
                        CellImpl cell = sheet.getCell("A1");
                        printEffectiveValue(cell, "A1");

                        try {
                            sheet.changeCell("A1", "{PLUS,a aa aa ,20}");
                        }
                        catch (Exception e) {
                            sheet = sheet.getSheetBeforeChange();
                        }
                         System.out.println("After a stupid try to change A1  ");
                        printEffectiveValue(sheet.getCell("A1"), "A1");
    }

            // Test 2: Simple reference (B1 = A1)

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
