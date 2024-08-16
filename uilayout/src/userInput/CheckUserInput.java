//package userInput;
//
//import FileCheck.STLSheet;
//import jakarta.xml.bind.JAXBException;
//import sheet.impl.CellImpl;
//import sheet.impl.SpreadSheetImpl;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//
//import static FileCheck.CheckForXMLFile.readXMLFile;
//import static FileCheck.CheckUserInputForFiles.checkFileUserInput;
//
//
//public class CheckUserInput {
//    private static final char FILE_INPUT = '1';
//    private static final char LOAD_CURRENT_SHEET = '2';
//    private static final char LOAD_SPECIFIC_CELL = '3';
//    private static final char UPDATE_SPECIFIC_CELL = '4';
//    private static final char VERSIONS_PRINT = '5';
//    private static final char EXIT_SYSTEM = '6';
//
//    public static void printMenu() {
//        System.out.print("Please choose an option from the menu: \n");
//        System.out.println("1. Load a new XML file");
//        System.out.println("2. Load current sheet");
//        System.out.println("3. Load specific cell");
//        System.out.println("4. Update specific cell");
//        System.out.println("5. Print versions");
//        System.out.println("6. Exit system");
//    }
//
//    public void UserStartMenuInput() throws JAXBException {
//        Scanner scanner = new Scanner(System.in);
//        File newFile = null;
//        Object result = null;
//        String input;
//        STLSheet sheet = null;
//        SpreadSheetImpl spreadSheet = null;
//        char userInput;
//        CellImpl cell = null;
//
//        do {
//            printMenu();
//            input = scanner.nextLine().trim();
//            while (input.isEmpty()) {
//                System.out.println("Input cannot be empty, please enter a valid option.");
//                input = scanner.nextLine().trim();
//            }
//
//            userInput = input.charAt(0);
//            while (userInput != FILE_INPUT && (newFile==null)) {
//                System.out.println("Please enter an xml file before trying to play");
//                newFile = checkFileUserInput();
//            }
//
//            switch (userInput) {
//                case FILE_INPUT:
//                    System.out.println("Please load a new XML file...");
//                    newFile = checkFileUserInput();
//                    sheet = readXMLFile(newFile.getAbsolutePath());
//                    spreadSheet = new SpreadSheetImpl(sheet);
//                    if(spreadSheet == null)
//                        throw(new RuntimeException("SpreadSheet is null"));
//                    break;
//                case LOAD_CURRENT_SHEET:
//                    System.out.println("Loading current sheet...");
//                    if(spreadSheet!= null) {
//                        printCurrentSheet(spreadSheet);
//                    }
//                    break;
//                case LOAD_SPECIFIC_CELL:
//                    if(spreadSheet!= null){
//                    System.out.println("Enter specific cell id:");
//                    String cellId = scanner.nextLine();
//                    cell = spreadSheet.getCell(cellId);
//                    System.out.println("Loading the cell...");
//                    printCell(cell);
//                    }
//                    break;
//                case UPDATE_SPECIFIC_CELL:
//                    if(spreadSheet!= null) {
//                        System.out.println("Updating the cell...");
//                        updateSpecificCell(spreadSheet);
//                    }
//                    break;
//                case VERSIONS_PRINT:
//                    System.out.println("Versions print to the system...");
//                    getVersionsList(spreadSheet);
//                    break;
//                case EXIT_SYSTEM:
//                    System.out.println("Exiting system...");
//                    break;
//                default:
//                    System.out.println("Invalid option, please enter a valid option.");
//                    break;
//            }
//        } while (userInput != EXIT_SYSTEM);
//        scanner.close();
//
//        exitSystem();
//    }
//
//
//
//    private void printCell(CellImpl cell) {
//        System.out.println("Cell id: " + cell.getId() + "\n");
//        System.out.println("Cell original Value: " + cell.getOriginalValue() + "\n");
//        System.out.println("Cell effective value: " + cell.getEffectiveValue() + "\n");
//        System.out.println("Cell last changed at version: " + cell.getLastChangeAt() + "\n");
//        System.out.println("Cell depending on: " + cell.getDependsOn() + "\n");
//        System.out.println("Cell affects cells: " + cell.getAffectsOn() + "\n");
//
//    }
//
//    public void printCurrentSheet(SpreadSheetImpl sheet) {
//        System.out.println("Sheet name: " + sheet.getSheetName() + "\n");
//        System.out.println("Sheet version: " + sheet.getSheetVersionNumber() + "\n");
//        printSheet(sheet);
//        System.out.println("\n");
//    }
//
//    public void exitSystem(){
//        System.exit(0);
//    }
//
//    public void updateSpecificCell(SpreadSheetImpl cell){
//        Object result = null;
//        System.out.println("Enter specific cell id: \n");
//        Scanner scanner = new Scanner(System.in);
//        scanner.reset();
//        result= scanner.nextLine();
//        System.out.println("Cell id: " + cell.getCell(result.toString()).getId() + "\n");
//        System.out.println("Cell original Value: " + cell.getCell(result.toString()).getOriginalValue() + "\n");
//        System.out.println("Cell effective value: " + cell.getCell(result.toString()).getEffectiveValue() + "\n");
//
//        System.out.println("Please enter new cell value: \n");
//        //cell.editCell(scanner.nextLine(), );
//    }
//    public void getVersionsList(SpreadSheetImpl sheet){
//        int versionNumber;
//        Map<Integer, SpreadSheetImpl> sheetMap;
//        sheetMap = sheet.getSheetMap();
//        System.out.println("Please enter the version number you want to peek at: \n");
//        Scanner scanner = new Scanner(System.in);
//        versionNumber = scanner.nextInt();
//        while (versionNumber < 0 || versionNumber > sheetMap.size()) {
//            System.out.println("Invalid choice, Please enter the version number you want to peek at: \n");
//            versionNumber = scanner.nextInt();
//        }
//        printCurrentSheet(sheetMap.get(versionNumber));
//    }
//
//    public void printSheet(SpreadSheetImpl sheet){
//        int colWidth = sheet.colWidth;
//        int rowHeight = sheet.rowHeight;
//        String spaceString = "  ".repeat(Math.max(1, colWidth+1));
//        String newLineString = " |\n".repeat(Math.max(1, rowHeight));
//        char letter;
//        System.out.print(" "); // Initial space for row numbers (aligns with row numbers)
//        for (int col = 0; col < 9; col++) {
//            letter = (char) ('A' + col % 26);
//            System.out.print("|" + spaceString + letter + spaceString);
//        }
//        // End divider
//        System.out.print(newLineString);
//
//// Print the grid rows with numbers and dividers
//        for (int row = 0; row < sheet.rowSize; row++) {
//            System.out.print((row + 1)); // Row number
//
//            for (int col = 0; col < sheet.columnSize; col++) {
//                letter = (char) ('A' + col % 26);
//                System.out.print("|" + spaceString + sheet.getCell(letter+String.valueOf(row+1)) + spaceString);
//            }
//            // End divider
//            System.out.print(newLineString);
//        }
//    }
//}
//
