package userInput;

import DTO.CellDataDTO;
import DTO.LoadDTO;
import DTO.sheetDTO;
import FileCheck.STLSheet;
import engine.impl.EngineImpl;
import java.io.File;
import java.util.Scanner;

import static FileCheck.CheckForXMLFile.getXMLFile;
import static FileCheck.CheckForXMLFile.loadXMLFile;

public class CheckUserInput {
    private static final String FILE_INPUT = "1";
    private static final String LOAD_CURRENT_SHEET = "2";
    private static final String LOAD_SPECIFIC_CELL = "3";
    private static final String UPDATE_SPECIFIC_CELL = "4";
    private static final String VERSIONS_PRINT = "5";
    private static final String EXIT_SYSTEM = "6";

    private EngineImpl engine;
    private Scanner scanner;
    private File newFile, oldFile;
    private String userInput;
    private STLSheet stlSheet;
    private sheetDTO sheetDto;
    private LoadDTO loadResult;
    private CellDataDTO cellData;

    public CheckUserInput() {
        engine = new EngineImpl();
        scanner = new Scanner(System.in);
    }

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
        do {
            printMenu();
            userInput = scanner.nextLine();
            while (!userInput.equals(FILE_INPUT) && (!engine.containSheet())) {
                if (userInput.equals(EXIT_SYSTEM)) {
                    System.out.println("Exiting system...");
                    System.exit(0);
                }
                System.out.println("Please enter an XML file before proceeding.");
                printMenu();
                userInput = scanner.nextLine();
            }

            switch (userInput) {
                case FILE_INPUT:
                    if (engine.containSheet()) {
                        System.out.println("A file is already loaded. Loading a new file will override it.");
                    }
                    System.out.println("Please load a new XML file - Enter a file path Or press Enter to go back to the main menu:");
                    userInput = scanner.nextLine();
                    if(userInput.isEmpty()) {
                        break;
                    }
                    newFile = checkFileUserInput();
                    loadResult = engine.Load(newFile);
                    if(loadResult.isNotValid()) {
                        if(!engine.containSheet()) {
                            System.out.println("Invalid file. Ensure it exists and it is an XML file.");
                            break;
                        }
                        else if(oldFile != null){
                            System.out.println("Invalid file. The previous file is retained.");
                            loadResult = engine.Load(oldFile);
                            break;
                        }
                    }
                    else {
                        oldFile = newFile;
                    }
                    stlSheet = loadXMLFile(loadResult.getLoadedFile());
                    try{
                        engine.initSheet(stlSheet);
                        break;
                    }
                   catch(Exception e){
                       System.out.println("Problem with Loading XML file.");
                        System.out.println(e.getMessage());
                        break;
                   }

                case LOAD_CURRENT_SHEET:
                    sheetDto = engine.Display();
                    printSheet(sheetDto);
                    break;

                case LOAD_SPECIFIC_CELL:
                    System.out.println("Enter specific cell id:");
                    String cellId = scanner.nextLine();
                    try {
                        cellData = engine.showCell(cellId);
                        printCell(cellData);
                    } catch (Exception noSuchCell) {
                        System.out.println(noSuchCell.getMessage());
                    }
                    break;

                case UPDATE_SPECIFIC_CELL:
                    System.out.println("Enter specific cell id:");
                    String cellToUpdate = scanner.nextLine();
                    System.out.println("Enter new cell value:");
                    String newValue = scanner.nextLine();
                    try {
                        sheetDto = engine.updateCell(cellToUpdate, newValue);
                        System.out.println("Cell updated.");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        sheetDto = engine.Display();
                    }
                    break;

                case VERSIONS_PRINT:
                    System.out.println("The versions print is:");

                    // Display all available versions with their respective active cell counts
                    for (int i = 1; i <= engine.getSheets().size(); i++) {
                        System.out.println("Version " + i + ": | Active cells: " + engine.getSheet(i).getActiveCells().size());
                    }

                    int versionNum;
                    try {
                        System.out.println("Please pick a version to peek at:");
                        versionNum = Integer.parseInt(scanner.nextLine());

                        // Validate if the input is within the valid range
                        if (versionNum >= 1 && versionNum <= engine.getSheets().size()) {
                            printSheet(engine.getSheet(versionNum));
                        } else {
                            System.out.println("Invalid version. Please enter a number between 1 and " + engine.getSheets().size() + ".");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                    break;

                case EXIT_SYSTEM:
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid option, please enter a valid option.");
                    break;
            }
        } while (!userInput.equals(EXIT_SYSTEM));

        System.exit(engine.exitSystem().getExitStatus());
    }
    public void printSheet(sheetDTO sheet) {
        String sheetName = sheet.getSheetName();
        String columnDivider = "|";
        String spaceString = " ".repeat(sheet.getColWidth());
        String newLineString = "\n".repeat(sheet.getRowHeight() + 1);
        char letter;

        System.out.println("Sheet name is: " + sheetName);
        System.out.println("Sheet version is: " + sheet.getSheetVersionNumber() + "\n");

        // Print column headers
        System.out.print(" ");
        for (int col = 0; col < sheet.getColSize(); col++) {
            letter = (char) ('A' + col % 26);
            System.out.print(spaceString + letter + spaceString + columnDivider);
        }
        // End divider
        System.out.print(newLineString);

        // Print the grid rows with numbers and dividers
        for (int row = 1; row <= sheet.getRowSize(); row++) {
            System.out.print(row); // Row number
            for (int col = 0; col < sheet.getColSize(); col++) {
                letter = (char) ('A' + col % 26);
                String cellKey = letter + String.valueOf(row);
                String cellValue;

                if (col == 0) {
                    System.out.print(columnDivider);
                }

                // Fetch and print the cell value, or an empty space if the cell does not exist
                CellDataDTO cell = sheet.getActiveCells().get(cellKey);
                if (cell != null) {
                    // Ensure cell value is treated as a string
                    Object valueObject = cell.getEffectiveValue().getValue();
                    cellValue = (valueObject != null) ? valueObject.toString() : "";

                    int paddingSize = sheet.getColWidth() * 2 + 1 - cellValue.length();
                    int leftPadding = paddingSize / 2;
                    int rightPadding = paddingSize - leftPadding;
                    System.out.print(" ".repeat(leftPadding) + cellValue + " ".repeat(rightPadding) + columnDivider);
                } else {
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
    public File checkFileUserInput() {
        File fileToCheck;
        fileToCheck = getXMLFile(userInput);
        return fileToCheck;
    }
}

