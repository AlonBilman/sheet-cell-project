package manager.api;

import dto.*;
import checkfile.STLSheet;
import manager.impl.SheetManagerImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SheetManager {

    // Initializes the spreadsheet with the given STLSheet
    void initSheet(STLSheet stlSheet);

    // Displays the current sheet
    SheetDto display();

    // Shows data for a specific cell
    CellDataDto showCell(String id);

    // Updates a specific cell with a new value
    SheetDto updateCell(String cellId, String value, boolean dynamically, String changedBy);

    // Sets the original value dynamically
    SheetDto setOriginalValDynamically(String cellId, String newOriginalVal);

    // Finalizes the dynamic change feature for a cell
    SheetDto finishedDynamicallyChangeFeature(String cellId);

    // Saves the current cell value
    void saveCellValue(String cellId);

    // Saves the spreadsheet state to a file
    void savePositionToFile(String folderPath, String fileName) throws IOException;

    // Sorts the spreadsheet based on the given parameters
    SheetDto sort(String params, List<String> sortBy);

    // Filters the spreadsheet based on the given parameters
    SheetDto filter(String params, Map<String, Set<String>> filterBy, SheetManagerImpl.OperatorValue operatorValue);

    // Checks if a sheet exists
    boolean containSheet();

    // Retrieves a map of sheets
    Map<Integer, SheetDto> getSheets();

    // Retrieves a specific sheet version
    SheetDto getSheet(int version);

    // Sets the text color of a cell
    void setTextColor(String id, String selectedColor);

    // Sets the background color of a cell
    void setBackgroundColor(String id, String selectedColor);

    // Retrieves a set of cell data from a dummy range
    Set<CellDataDto> getSetOfCellsDtoDummyRange(String params);
}
