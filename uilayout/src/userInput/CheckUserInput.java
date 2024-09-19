package userInput;

import dto.CellDataDTO;
import dto.LoadDTO;
import dto.RangeDTO;
import dto.sheetDTO;
import checkfile.STLSheet;
import engine.impl.EngineImpl;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import static checkfile.CheckForXMLFile.*;

public class CheckUserInput {
    private static final String FILE_INPUT = "1";
    private static final String LOAD_CURRENT_SHEET = "2";
    private static final String LOAD_SPECIFIC_CELL = "3";
    private static final String UPDATE_SPECIFIC_CELL = "4";
    private static final String VERSIONS_PRINT = "5";
    private static final String SAVE_SHEET = "6";
    private static final String LOAD_SAVED_SHEET = "7";
    private static final String EXIT_SYSTEM = "8";
    private static final String ADD_RANGE = "9";
    private static final String PRINT_RANGE = "10";

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
        String[] options = {
                "Load a new XML file",
                "Load current sheet",
                "Load specific cell",
                "Update specific cell",
                "Print sheet versions",
                "Save sheet",
                "Load saved sheet",
                "Exit system",
                "range",
                "print range"
        };

        // Print the menu header
        System.out.println("+------------------------------------------+");
        System.out.println("|          Please choose an option         |");
        System.out.println("+------------------------------------------+");

        // Print the menu options
        for (int i = 0; i < options.length; i++) {
            String option = String.format("| %2d. %-34s   |", i + 1, options[i]);
            System.out.println(option);
        }

        // Print the menu footer
        System.out.println("+------------------------------------------+");
    }

    public void UserStartMenuInput() {
        do {
            printMenu();
            userInput = scanner.nextLine().trim();
            while ((!userInput.equals(FILE_INPUT) && !userInput.equals(LOAD_SAVED_SHEET)) && !engine.containSheet()
            ) {
                if (userInput.equals(EXIT_SYSTEM)) {
                    System.out.println("Exiting system...");
                    System.exit(0);
                }
                System.out.println("Please enter an XML file before proceeding.\n" +
                        "Or load an existing saved sheet-file");
                printMenu();
                userInput = scanner.nextLine().trim();
            }

            switch (userInput) {
                case FILE_INPUT:
                    if (engine.containSheet()) {
                        System.out.println("A file is already loaded. Loading a new file will override it.");
                    }
                    System.out.println("Please load a new XML file - Enter a file path Or press Enter to go back to the main menu:");
                    userInput = scanner.nextLine().trim();
                    if (userInput.isEmpty()) {
                        break;
                    }
                    newFile = checkFileUserInput();
                    loadResult = engine.Load(newFile);
                    if (loadResult.isNotValid()) {
                        if (!engine.containSheet()) {
                            System.out.println("Invalid file. Ensure it exists and it is an XML file.");
                            break;
                        } else if (oldFile != null) {
                            System.out.println("Invalid file. The previous file is retained.");
                            loadResult = engine.Load(oldFile);
                            break;
                        }
                    } else {
                        oldFile = newFile;
                    }
                    stlSheet = loadXMLFile(loadResult.getLoadedFile());
                    try {
                        engine.initSheet(stlSheet);
                        System.out.println("XML File loaded.");
                        break;
                    } catch (Exception e) {
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
                    String cellId = scanner.nextLine().trim();
                    try {
                        cellData = engine.showCell(cellId);
                        printCell(cellData);
                    } catch (Exception noSuchCell) {
                        System.out.println(noSuchCell.getMessage());
                    }
                    break;

                case UPDATE_SPECIFIC_CELL:
                    System.out.println("Enter specific cell id:");
                    String cellToUpdate = scanner.nextLine().trim();
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
                        versionNum = Integer.parseInt(scanner.nextLine().trim());

                        // Validate if the input is within the valid range
                        if (versionNum >= 1 && versionNum <= engine.getSheets().size()) {
                            printSheet(engine.getSheet(versionNum));
                        } else {
                            System.out.println("Invalid version number. Please enter a version between 1 and " + engine.getSheets().size() + ".");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number, according to the version table options.");
                    }
                    break;
                case SAVE_SHEET:
                    System.out.println("Please enter the absolute path to the file you would like to save:");
                    String newFilePath = scanner.nextLine().trim();
                    System.out.println("Next, please enter the name of the file you would like to save as:");
                    String fileName = scanner.nextLine().trim();
                    try {
                        engine.savePositionToFile(newFilePath, fileName);
                        System.out.println("File saved");
                    } catch (Exception e) {
                        System.out.println("Could not save the file. make sure that the path is correct\n"
                                + e.getMessage());
                    }
                    break;
                case LOAD_SAVED_SHEET:
                    System.out.println("Loading a saved sheet...");
                    System.out.println("Please enter the file path:");
                    String filePathToLoad = scanner.nextLine().trim();
                    System.out.println("Please Enter the name of the file you would like to load (without .<fileType>)");
                    String fileNameToLoad = scanner.nextLine().trim();
                    try {
                        engine = EngineImpl.resumePositionToEngine(filePathToLoad, fileNameToLoad);
                        oldFile = new File(filePathToLoad);
                        System.out.println("File loaded.");
                    } catch (Exception e) {
                        System.out.println("Something went wrong when loading the saved sheet." +
                                "\n" + e.getMessage());
                    }
                    break;
                case EXIT_SYSTEM:
                    System.out.println("Exiting system...");
                    break;

                case ADD_RANGE:
                    System.out.println("name of range? :");
                    String rangeName = scanner.nextLine().trim();
                    System.out.println("from where to where?");
                    String rangeParams = scanner.nextLine().trim();
                    try {
                        // Assuming addRange method returns a RangeDTO object
                        engine.addRange(rangeName, rangeParams);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case "11":
                    System.out.println("name of range? :");
                    String rangeName2 = scanner.nextLine().trim();
                    try {
                        engine.deleteRange(rangeName2);
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "12":
                    System.out.println("range :");
                    String rangeName3 = scanner.nextLine().trim();
                    System.out.println("col to sort");
                    String colToSortParams = scanner.nextLine().trim();
                    List<String> list = List.of(colToSortParams.split("\\s+"));
                    try {
                        printSheet(engine.sort(rangeName3, list));
                    }catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case PRINT_RANGE:
                    System.out.println("Enter the name of the range you want to print:");
                    String rangeToPrint = scanner.nextLine().trim();
                    try {
                        RangeDTO range = engine.getRangeDto(rangeToPrint);
                        printRange(range);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
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
        char letter;
        System.out.println("Sheet name is: " + sheetName);
        System.out.println("Sheet version is: " + sheet.getSheetVersionNumber() + "\n");
        // Print column headers with correct alignment
        System.out.print("  |"); // Two spaces for alignment with row numbers
        for (int col = 0; col < sheet.getColSize(); col++) {
            letter = (char) ('A' + col % 26);
            String header = String.valueOf(letter);
            System.out.print(fitToWidth(header, sheet.getColWidth()) + columnDivider);
        }
        System.out.print("\n".repeat(sheet.getRowHeight()));

        // Print the grid rows with numbers and dividers
        for (int row = 1; row <= sheet.getRowSize(); row++) {
            // Format the row number with leading zeros
            String formattedRowNumber = String.format("%02d", row);
            System.out.print(formattedRowNumber);
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
                    cellValue = cell.getEffectiveValue().getValue().toString();
                    System.out.print(fitToWidth(cellValue, sheet.getColWidth()) + columnDivider);
                } else {
                    System.out.print(" ".repeat(sheet.getColWidth()) + columnDivider);
                }
            }
            // End divider
            System.out.print("\n".repeat(sheet.getRowHeight()));
        }
    }


    // Helper method to fit text to a specific width with padding
    private String fitToWidth(String text, int width) {
        if (text.length() > width) {
            return text.substring(0, width); // Cut off the text if it's too long
        } else {
            return String.format("%-" + width + "s", text); // Pad with spaces to the right if it's too short
        }
    }

    void printCell(CellDataDTO cell) {
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

    public void printRange(RangeDTO rangeDto) {
        System.out.println("Range name: " + rangeDto.getName());
        System.out.println("Cells in range:");

        for (CellDataDTO cell : rangeDto.getCells()) {
            System.out.print(cell.getId()+",");
        }
        System.out.println();
    }

}