package sheet.impl;

import sheet.impl.Range;
import sheet.impl.SpreadSheetImpl;

public class mainTest {
    public static void main(String[] args) {
        // Initialize the spreadsheet with custom parameters
        String sheetName = "TestSheet";
        int rowSize = 10; // 10 rows
        int columnSize = 10; // 10 columns
        int colWidth = 15;
        int rowHeight = 20;

        SpreadSheetImpl spreadsheet = new SpreadSheetImpl(sheetName, rowSize, columnSize, colWidth, rowHeight);

        // Add some example cells to the spreadsheet
        addSampleCells(spreadsheet);

        // Test different kinds of ranges

        // 1. Single-cell range
        testRange(spreadsheet, "SingleCell", "A1", "A1");

        // 2. Entire row range
        testRange(spreadsheet, "EntireRow", "A2", "J2");

        // 3. Entire column range
        testRange(spreadsheet, "EntireColumn", "C1", "C10");

        // 4. Multi-cell rectangular range
        testRange(spreadsheet, "Rectangle", "B2", "E5");

        // 5. Large multi-cell rectangular range (covers almost the entire sheet)
        testRange(spreadsheet, "LargeRectangle", "A1", "J10"); //100

        // 6. Edge case: Last row and column
        testRange(spreadsheet, "EdgeCase", "J10", "J10"); //1

        // 7. Invalid range: Top-left cell is greater than bottom-right cell
        try {
            testRange(spreadsheet, "InvalidRange", "E5", "B2");
        } catch (RuntimeException e) {
            System.out.println("\n\nCaught expected exception for invalid range: " + e.getMessage());
        }
    }

    private static void addSampleCells(SpreadSheetImpl spreadsheet) {
        // Populate the spreadsheet with some values
//        spreadsheet.changeCell("A1", "10");
//        spreadsheet.changeCell("A2", "20");
//        spreadsheet.changeCell("B1", "30");
//        spreadsheet.changeCell("B2", "40");
//        spreadsheet.changeCell("C3", "50");
//        spreadsheet.changeCell("D4", "60");
//        spreadsheet.changeCell("E5", "70");
//        spreadsheet.changeCell("F6", "80");
//        spreadsheet.changeCell("G7", "90");
//        spreadsheet.changeCell("H8", "100");
//        spreadsheet.changeCell("I9", "110");
//        spreadsheet.changeCell("J10", "120");
    }

    private static void testRange(SpreadSheetImpl spreadsheet, String rangeName, String topLeftCellId, String bottomRightCellId) {
        System.out.println("Testing range: " + rangeName);
        spreadsheet.addRange(rangeName, topLeftCellId, bottomRightCellId);
        Range range = spreadsheet.getRange(rangeName);
        if (range != null) {
            System.out.println("Cells in range \"" + rangeName + "\":");
            //spreadsheet.printRange(range);
            System.out.println();
        } else {
            System.out.println("Range \"" + rangeName + "\" not found.");
            System.out.println();
        }
        System.out.println();
    }
}
