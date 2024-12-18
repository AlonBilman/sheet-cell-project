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
    public static final String ALL_VERSIONS = "/all-versions";
    public static final String RANGE = "/range";
    public static final String SORT = "/sort";
    public static final String FILTER = "/filter";
    public static final String NO_NAME_RANGE = "/no-name-range";
    public static final String DYNAMIC_CHANGE = "/dynamic-change";
    public static final String NO_NAME_RANGES = "/no-name-ranges";
    public static final Integer REFRESH_RATE = 1500;
    public static final String USERS = "/users";
    public static final String PERMISSIONS = "/permissions";
    public static final String OWNER = "owner";
    public static final String REQUESTER = "requester";
    public static final String VERSION = "/version";
    public static final String CHAT_LINES_LIST = "/chat-lines-list";
    public static final String WRITE_TO_CHAT = "/write-to-chat";
    public static final String VERSION_PARAM = "version";
    public final static String CHAT_LINE_FORMATTING = "[%tH:%tM:%tS]  %.10s: %s%n";
}
