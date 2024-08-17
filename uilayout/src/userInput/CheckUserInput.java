package userInput;

import FileCheck.STLSheet;
import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;

import java.awt.desktop.ScreenSleepEvent;
import java.io.File;
import java.util.Map;
import java.util.Scanner;

import static FileCheck.CheckForXMLFile.readXMLFile;
import static FileCheck.CheckUserInputForFiles.checkFileUserInput;


public class CheckUserInput {
    private static final char FILE_INPUT = '1';
    private static final char LOAD_CURRENT_SHEET = '2';
    private static final char LOAD_SPECIFIC_CELL = '3';
    private static final char UPDATE_SPECIFIC_CELL = '4';
    private static final char VERSIONS_PRINT = '5';
    private static final char EXIT_SYSTEM = '6';

    public static void printMenu() {
        // ANSI escape codes for bold and reset
        final String BOLD = "\033[1m";
        final String ITALIC = "\033[3m";
        final String RESET = "\033[0m";

        // Define the menu options and their descriptions
        String[] options = {
                "Load a new XML file",
                "Load current sheet",
                "Load specific cell",
                "Update specific cell",
                "Print sheet versions",
                "Exit system"
        };

        // Print the menu header
        System.out.println("+------------------------------------------+");
        System.out.println("|" + BOLD + BOLD+  "          Please choose an option" + RESET + "         |");
        System.out.println("+------------------------------------------+");

        // Print the menu options
        for (int i = 0; i < options.length; i++) {
            String option = String.format("| %2d. %s%-34s%s   |", i + 1, ITALIC, options[i], RESET);
            System.out.println(option);
        }

        // Print the menu footer
        System.out.println("+------------------------------------------+");

    }

    public void UserStartMenuInput() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        File newFile = null, oldFile = null;
        String input;
        STLSheet sheet = null;
        SpreadSheetImpl spreadSheet = null;
        char userInput;
        CellImpl cell = null;
        do {
            printMenu();
            input = scanner.nextLine();
            while (input.isEmpty()) {
                System.out.println("Input cannot be empty, please enter a valid option.");
                input = scanner.nextLine();
            }
            userInput = input.charAt(0);
            while (userInput != FILE_INPUT && (oldFile==null)) {
                if(userInput != EXIT_SYSTEM) {
                    System.out.println("Please enter an xml file before trying to play");
                    newFile = checkFileUserInput();
                    userInput = FILE_INPUT;
                }
                else{
                    System.out.println("Exiting system");
                    System.exit(0);
                }
            }

            switch (userInput) {
                case FILE_INPUT:
                    System.out.println("Please load a new XML file...");
                    newFile = checkFileUserInput();
                    while (newFile == null && oldFile == null) {
                        System.out.println("Invalid file. Ensure it exists and is an XML file. Please try again.");
                        newFile = checkFileUserInput();
                    }
                    if(oldFile==null) {

                        oldFile = newFile;
                    }
                    else{

                        if(newFile == null){
                            System.out.println("Wrong XML file, old one was saved");
                        }
                        else {
                            System.out.println("New xml file loaded.");
                            oldFile = newFile;
                        }
                    }
                    sheet = readXMLFile(oldFile.getAbsolutePath());
                    spreadSheet = new SpreadSheetImpl(sheet);
                    if(spreadSheet == null)
                        throw(new RuntimeException("SpreadSheet is null"));
                    break;
                case LOAD_CURRENT_SHEET:
                    System.out.println("Loading current sheet...");
                    if(spreadSheet!= null) {
                        printCurrentSheet(spreadSheet);
                    }
                    break;
                case LOAD_SPECIFIC_CELL:
                    if(spreadSheet!= null){
                    System.out.println("Enter specific cell id:");
                    String cellId = scanner.nextLine();
                    cell = spreadSheet.getCell(cellId);
                    System.out.println("Loading the cell...");
                    printCell(cell);
                    }
                    break;
                case UPDATE_SPECIFIC_CELL:
                    if(spreadSheet!= null) {
                        System.out.println("Updating the cell...");
                        updateSpecificCell(spreadSheet);
                    }
                    break;
                case VERSIONS_PRINT:
                    System.out.println("Versions print to the system...");
                    getVersionsList(spreadSheet);
                    break;
                case EXIT_SYSTEM:
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid option, please enter a valid option.");
                    break;
            }

        } while (userInput != EXIT_SYSTEM);

        exitSystem();
    }



    private void printCell(CellImpl cell) {
        System.out.println("Cell id: " + cell.getId() + "\n");
        System.out.println("Cell original Value: " + cell.getOriginalValue() + "\n");
        System.out.println("Cell effective value: " + cell.getEffectiveValue() + "\n");
        System.out.println("Cell last changed at version: " + cell.getLastChangeAt() + "\n");
        System.out.println("Cell depending on: " + cell.getDependsOn() + "\n");
        System.out.println("Cell affects cells: " + cell.getAffectsOn() + "\n");

    }

    public void printCurrentSheet(SpreadSheetImpl sheet) {
        System.out.println("Sheet name: " + sheet.getSheetName() + "\n");
        System.out.println("Sheet version: " + sheet.getSheetVersionNumber() + "\n");
        printSheet(sheet);
        System.out.println("\n");
    }

    public void exitSystem(){
        System.exit(0);
    }

    public void updateSpecificCell(SpreadSheetImpl cell){
        Object result = null;
        System.out.println("Enter specific cell id: \n");
        Scanner scanner = new Scanner(System.in);
        scanner.reset();
        result= scanner.nextLine();
        System.out.println("Cell id: " + cell.getCell(result.toString()).getId() + "\n");
        System.out.println("Cell original Value: " + cell.getCell(result.toString()).getOriginalValue() + "\n");
        System.out.println("Cell effective value: " + cell.getCell(result.toString()).getEffectiveValue() + "\n");

        System.out.println("Please enter new cell value: \n");
        //cell.editCell(scanner.nextLine(), );
    }
    public void getVersionsList(SpreadSheetImpl sheet){
        int versionNumber;
        Map<Integer, SpreadSheetImpl> sheetMap;
        sheetMap = sheet.getSheetMap();
        System.out.println("Please enter the version number you want to peek at: \n");
        Scanner scanner = new Scanner(System.in);
        versionNumber = scanner.nextInt();
        while (versionNumber < 0 || versionNumber > sheetMap.size()) {
            System.out.println("Invalid choice, Please enter the version number you want to peek at: \n");
            versionNumber = scanner.nextInt();
        }
        printCurrentSheet(sheetMap.get(versionNumber));
    }

    public void printSheet(SpreadSheetImpl sheet){
        int colWidth = sheet.getColWidth();
        int rowHeight = sheet.getRowHeight();
        String columnDivider = "|";
        String spaceString = "  ".repeat(Math.max(1, colWidth));
        String newLineString = " |\n".repeat(Math.max(1, rowHeight));
        char letter;
        System.out.print(" "); // Initial space for row numbers (aligns with row numbers)
        for (int col = 0; col < sheet.getColumnSize(); col++) {
            letter = (char) ('A' + col % 26);
            System.out.print("|" + spaceString + letter + spaceString);
        }
        // End divider
        System.out.print(newLineString);

// Print the grid rows with numbers and dividers
        for (int row = 0; row < sheet.getRowSize(); row++) {
            System.out.print((row + 1)); // Row number

            for (int col = 0; col < sheet.getColumnSize(); col++) {
                letter = (char) ('A' + col % 26);
                try{
                    System.out.print("|" + spaceString + sheet.getCell(letter+String.valueOf(row+1)).getEffectiveValue().getValue() + spaceString);
                }catch (Exception noSuchCell){
                    System.out.print("|" + spaceString + " " + spaceString);
                }


            }
            // End divider
            System.out.print(newLineString);
        }
    }
}

