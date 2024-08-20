package userInput;

import DTO.CellDataDTO;
import DTO.LoadDTO;
import DTO.sheetDTO;
import FileCheck.STLSheet;
import sheet.impl.CellImpl;
import sheet.impl.SpreadSheetImpl;
import engineImpl.EngineImpl;
import java.io.File;
import java.util.Scanner;

import static FileCheck.CheckForXMLFile.getXMLFile;
import static FileCheck.CheckForXMLFile.loadXMLFile;

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

    public void UserStartMenuInput() {
        EngineImpl engine = new EngineImpl();
        Scanner scanner = new Scanner(System.in);
        File newFile = null, oldFile = null;
        String input;
        STLSheet stlSheet;
        sheetDTO sheet = null;
        char userInput = 0;
        LoadDTO loadResult = null;
        SpreadSheetImpl spreadSheet = null;
        CellDataDTO cellData;

        do {
            printMenu();
            input = scanner.nextLine();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty, please enter a valid option.");
                continue;  // Prompt the user again
            }


            userInput = input.charAt(0);
            while (userInput != FILE_INPUT && (spreadSheet == null)) {
                if (userInput != EXIT_SYSTEM) {
                    System.out.println("Please enter an XML file before proceeding.");
                    oldFile = checkFileUserInput();
                    userInput = FILE_INPUT;
                } else {
                    System.out.println("Exiting system...");
                    System.exit(0);
                }
            }

            switch (userInput) {
                case FILE_INPUT:
                    do {
                        if (spreadSheet != null) {
                            System.out.println("A file is already loaded. Loading a new file will override it.");
                        }

                        System.out.println("Please load a new XML file...");
                        newFile = checkFileUserInput();
                        loadResult = engine.Load(newFile);

                        if (loadResult.isNotValid() && spreadSheet == null) {
                            System.out.println("Invalid file. Ensure it exists and is an XML file.");
                        }
                    } while (loadResult.isNotValid() && oldFile == null);

                    if (!loadResult.isNotValid()) {
                        oldFile = newFile;
                    }

                    if (loadResult.isNotValid() && oldFile != null) {
                        System.out.println("Invalid file. The previous file is retained.");
                        loadResult = engine.Load(oldFile);
                    }

                    stlSheet = loadXMLFile(loadResult.getLoadedFile());
                    spreadSheet = new SpreadSheetImpl(stlSheet);

                    break;

                case LOAD_CURRENT_SHEET:
                    sheet = engine.Display(spreadSheet);
                    printSheet(sheet);
                    break;

                case LOAD_SPECIFIC_CELL:
                    System.out.println("Enter specific cell id:");
                    String cellId = scanner.nextLine();
                    try {
                        cellData = engine.showCell(spreadSheet.getCell(cellId));
                        printCell(cellData);
                    } catch (Exception noSuchCell) {
                        System.out.println("Specific cell id not found. Please try again.");
                    }
                    break;

                case UPDATE_SPECIFIC_CELL:
                    System.out.println("Enter specific cell id:");
                    String cellToUpdate = scanner.nextLine();
                    System.out.println("Enter new cell value:");
                    String newValue = scanner.nextLine();
                    try {
                        sheet = engine.updateCell(spreadSheet, cellToUpdate, newValue);
                        System.out.println("Cell updated.");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        sheet = new sheetDTO(spreadSheet);  // This is redundant but safe
                    }
                    break;

                case VERSIONS_PRINT:
                    System.out.println("The versions print is:");
                    sheetDTO versionsSheet = engine.showVersions(spreadSheet);
                    int versionNum;
                    for (int i = 1; i < versionsSheet.getSheetMap().size(); i++) {
                        System.out.println("version " + i + ": " + " | " + "Active cells: " + versionsSheet.getSheetMap().get(i).getActiveCells().size());
                        System.out.println("Please pick a version to peek at");
                        scanner = new Scanner(System.in);
                        versionNum = scanner.nextInt();
                        while (versionNum < 0 || versionNum > versionsSheet.getSheetMap().get(i).getActiveCells().size()) {
                            System.out.println("Invalid version. Please try again.");
                            versionNum = scanner.nextInt();
                        }
                        printSheet(versionsSheet.getSheetMap().get(versionNum));
                    }

                    break;

                case EXIT_SYSTEM:
                    System.out.println("Exiting system...");
                    break;

                default:
                    System.out.println("Invalid option, please enter a valid option.");
                    break;
            }
        } while (userInput != EXIT_SYSTEM);

        System.exit(engine.exitSystem().getExitStatus());
    }

    public void printSheet(sheetDTO sheet){
        String sheetName = sheet.getSheetName();
        String columnDivider = "|";
        String spaceAfterString;
        String spaceString = "  ".repeat(sheet.getColWidth());
        String newLineString = "\n".repeat(sheet.getRowHeight()+1);
        char letter;
        System.out.println("Sheet name is: " + sheetName);
        System.out.println("Sheet version is: " + sheet.getSheetVersionNumber() + "\n");
        System.out.print(" ");
        for (int col = 0; col < sheet.getColSize(); col++) {
            letter = (char) ('A' + col % 26);
            if(col == 0)
                System.out.print(columnDivider);
            System.out.print(spaceString + letter + spaceString+ columnDivider);
        }
        // End divider
        System.out.print(newLineString);

// Print the grid rows with numbers and dividers
        for (int row = 1; row <= sheet.getRowSize(); row++) {
            System.out.print((row)); // Row number
            for (int col = 0; col < sheet.getColSize(); col++) {
                letter = (char) ('A' + col % 26);
                if(col == 0)
                    System.out.print(columnDivider);
                try{
                    spaceAfterString = " ".repeat(sheet.getColWidth() * 2 - sheet.getActiveCells().get(letter+String.valueOf(row)).getOriginalValue().length());
                    System.out.print(spaceString + sheet.getActiveCells().get(letter+String.valueOf(row)).getEffectiveValue().getValue() + spaceAfterString + columnDivider);
                }catch (Exception noSuchCell){
                    System.out.print(spaceString + " " + spaceString + columnDivider);
                }

            }
            // End divider
            System.out.print(newLineString);

        }
    }

    public void printCell(CellDataDTO cell) {
        System.out.println("Cell id: " + cell.getId() + "\n");
        System.out.println("Cell original Value: " + cell.getOriginalValue() + "\n");
        System.out.println("Cell effective value: " + cell.getEffectiveValue().getValue() + "\n");
        System.out.println("Cell last changed at version: " + cell.getLastChangeAt() + "\n");
        System.out.println("Cell depending on: " + cell.getDependsOn() + "\n");
        System.out.println("Cell affects cells: " + cell.getAffectsOn() + "\n");

    }

    public static File checkFileUserInput() {
        String filePath;
        Scanner scanner = new Scanner(System.in);
        File fileToCheck;


        System.out.print("Enter the file path: ");
        filePath = scanner.nextLine();
        fileToCheck = getXMLFile(filePath);


        return fileToCheck;
    }
}