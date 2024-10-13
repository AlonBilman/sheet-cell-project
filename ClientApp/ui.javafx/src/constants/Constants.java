package constants;

import com.google.gson.Gson;

public class Constants {
    public final static Gson GSON = new Gson();
    public final static String BASE_DIRECTORY = "http://localhost:8080/SheetCell";
    public final static String LOGIN = "/login";
    public final static String LOADFILE = "/load-file";
    public final static String LOGOUT = "/logout";
    public final static String DISPLAY = "/display";
    public final static String SHEET_DTO = "/sheet-dto";
    public final static String SHEET_ID = "sheetId";
    public final static String CELL_ID = "cellId";
    public final static String CELL_DTO = "/cell-dto";
    public final static String CELL = "/cell";
    public final static String MODIFY = "/modify";
    public static final String CELL_TEXT_COLOR = "/cell-text-color";
    public static final String CELL_BACKGROUND_COLOR = "/cell-background-color";
}
