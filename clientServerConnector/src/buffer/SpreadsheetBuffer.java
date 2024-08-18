package buffer;

import FileCheck.STLSheet;
import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import static FileCheck.CheckForXMLFile.readXMLFile;

public class SpreadsheetBuffer {
    private SpreadSheetImpl spreadSheet = null;

    public void loadXMLFile(File file) {
            STLSheet sheet = readXMLFile(file.getAbsolutePath());
            this.spreadSheet = new SpreadSheetImpl(sheet);

    }

    public SpreadSheetImpl getCurrentSheet() {
        return this.spreadSheet;
    }

    public CellImpl getCell(String cellId) {
        return this.spreadSheet != null ? this.spreadSheet.getCell(cellId) : null;
    }

    public void updateCell(String cellId, String newValue) {
        if (this.spreadSheet != null) {
            CellImpl cell = this.spreadSheet.getCell(cellId);
            cell.editCell(newValue, this.getCurrentSheet().getSheetVersionNumber()); // Adjust this method as needed
        }
    }

    public void getSheetVersions() {
        Map<Integer, SpreadSheetImpl> sheetVersions = this.spreadSheet.getSheetMap();
        for (int i = 1; i <= sheetVersions.size(); i++) {
            printSheet(sheetVersions.get(i));
        }
        System.out.println("Choose version u want to peek at: ");
        Scanner scanner = new Scanner(System.in);
        int version = scanner.nextInt();
        while (version < 1 || version > sheetVersions.size()) {
            System.out.println("Invalid version number, please try again");
            version = scanner.nextInt();
        }
        this.spreadSheet = sheetVersions.get(version);
    }

    public void printCurrentSheet() {
        System.out.println("Sheet name: " + this.spreadSheet.getSheetName() + "\n");
        System.out.println("Sheet version: " + this.spreadSheet.getSheetVersionNumber() + "\n");
        printSheet(this.spreadSheet);
        System.out.println("\n");
    }

    public void exitSystem(){
        System.exit(0);
    }

    public void updateSpecificCell(SpreadSheetImpl cell){
        Object result = null;
        System.out.println("Enter specific cell id: \n");
        Scanner scanner = new Scanner(System.in);
        result= scanner.nextLine();
        System.out.println("Cell id: " + cell.getCell(result.toString()).getId() + "\n");
        System.out.println("Cell original Value: " + cell.getCell(result.toString()).getOriginalValue() + "\n");
        System.out.println("Cell effective value: " + cell.getCell(result.toString()).getEffectiveValue() + "\n");

        System.out.println("Please enter new cell value: \n");
        //cell.editCell(scanner.nextLine(), );
    }


    public void printSheet(SpreadSheetImpl sheet){
        int colWidth = sheet.getColWidth();
        int rowHeight = sheet.getRowHeight();
        String columnDivider = "|";
        String spaceString = "  ".repeat(Math.max(1, colWidth));
        String newLineString = "\n".repeat(Math.max(1, rowHeight+1));
        char letter;
        System.out.print(" "); // Initial space for row numbers (aligns with row numbers)
        for (int col = 0; col < sheet.getColumnSize(); col++) {
              letter = (char) ('A' + col % 26);
              if(col == 0)
                  System.out.print(columnDivider);
            System.out.print(spaceString + letter + spaceString+ columnDivider);
        }
        // End divider
        System.out.print(newLineString);

// Print the grid rows with numbers and dividers
        for (int row = 0; row < sheet.getRowSize(); row++) {
            System.out.print((row + 1)); // Row number
            for (int col = 0; col < sheet.getColumnSize(); col++) {
                letter = (char) ('A' + col % 26);
                if(col == 0)
                    System.out.print(columnDivider);
                try{
                    System.out.print(spaceString + sheet.getCell(letter+String.valueOf(row)).getEffectiveValue().getValue() + spaceString + columnDivider);
                }catch (Exception noSuchCell){
                    System.out.print(spaceString + " " + spaceString + columnDivider);
                }

            }
            // End divider
                System.out.print(newLineString);

        }
    }

    public void printCell(CellImpl cell) {
        System.out.println("Cell id: " + cell.getId() + "\n");
        System.out.println("Cell original Value: " + cell.getOriginalValue() + "\n");
        System.out.println("Cell effective value: " + cell.getEffectiveValue().getValue() + "\n");
        System.out.println("Cell last changed at version: " + cell.getLastChangeAt() + "\n");
        System.out.println("Cell depending on: " + cell.getDependsOn() + "\n");
        System.out.println("Cell affects cells: " + cell.getAffectsOn() + "\n");

    }
}