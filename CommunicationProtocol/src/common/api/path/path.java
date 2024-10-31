package common.api.path;

import com.google.gson.Gson;

public class path {
    // General path
    public static final Gson GSON = new Gson();
    public static final Integer REFRESH_RATE = 1500;
    public static final String CHAT_LINE_FORMATTING = "[%tH:%tM:%tS]  %.10s: %s%n";
    // Paths
    public static final String BASE_DIRECTORY = "http://localhost:8080/SheetCell";
    public static final String START_UP = "";
    public static final String LOGIN = "/login";
    public static final String LOADFILE = "/load-file";
    public static final String LOGOUT = "/logout";
    public static final String DISPLAY = "/display";
    public static final String SHEET_DTO = "/sheet-dto";
    public static final String CELL_DTO = "/cell-dto";
    public static final String CELL = "/cell";
    public static final String MODIFY = "/modify";
    public static final String CELL_TEXT_COLOR = "/cell-text-color";
    public static final String CELL_BACKGROUND_COLOR = "/cell-background-color";
    public static final String ALL_VERSIONS = "/all-versions";
    public static final String RANGE = "/range";
    public static final String SORT = "/sort";
    public static final String FILTER = "/filter";
    public static final String DYNAMICALLY = "/dynamically";
    public static final String NO_NAME_RANGE = "/no-name-range";
    public static final String DYNAMIC_CHANGE = "/dynamic-change";
    public static final String NO_NAME_RANGES = "/no-name-ranges";
    public static final String USERS = "/users";
    public static final String PERMISSIONS = "/permissions";
    public static final String VERSION = "/version";
    public static final String CHAT_LINES_LIST = "/chat-lines-list";
    public static final String WRITE_TO_CHAT = "/write-to-chat";

    // Parameters
    public static final String SHEET_ID = "sheetId";
    public static final String CELL_ID = "cellId";
    public static final String VERSION_PARAM = "version";
    public static final String OWNER = "owner";
    public static final String REQUESTER = "requester";
}

/*

package constants;

public class Constants {
    public static final String ENGINE = "engine";
    public static final String USERNAME = "username";
    public static final String ENGINE_SERVLET = "EngineServlet";
    public static final String LOGIN_SERVLET = "LoginServlet";
    public static final String SHEET_SERVLET = "SheetServlet";
    public static final String COLOR_SERVLET = "ColorServlet";
    public static final String FILTER_SERVLET = "FilterServlet";
    public static final String SORT_SERVLET = "SortServlet";
    public static final String NO_NAME_RANGE_SERVLET = "NoNameRangeServlet";
    public static final String START_UP = "";
    public static final String LOGIN = "/login";
    public static final String DISPLAY = "/display";
    public static final String MODIFY = "/modify";
    public static final String SHEET_DTO = "/sheet-dto";
    public static final String LOADFILE = "/load-file";
    public static final String LOADFILE_SERVLET = "LoadFileServlet";
    public static final String SHEET_ID = "sheetId";
    public static final String LOGOUT_SERVLET = "LogoutServlet";
    public static final String LOGOUT = "/logout";
    public static final String CELL_ID = "cellId";
    public static final String CELL_DTO = "/cell-dto";
    public static final String CELL_SERVLET = "CellServlet";
    public static final String CELL_TEXT_COLOR = "/cell-text-color";
    public static final String CELL_BACKGROUND_COLOR = "/cell-background-color";
    public static final String CELL = "/cell";
    public static final String RANGE_SERVLET = "RangeServlet";
    public static final String RANGE = "/range";
    public static final String ALL_VERSIONS = "/all-versions";
    public static final String DYNAMICALLY = "/dynamically";
    public static final String SORT = "/sort";
    public static final String FILTER = "/filter";
    public static final String NO_NAME_RANGE = "/no-name-range";
    public static final String DYNAMIC_CHANGE_SERVLET = "DynamicChangeServlet";
    public static final String DYNAMIC_CHANGE = "/dynamic-change";
    public static final String NO_NAME_RANGES = "/no-name-ranges";
    public static final String USERS = "/users";
    public static final String USERS_SERVLET = "UsersServlet";
    public static final String PERMISSION_SERVLET = "PermissionServlet";
    public static final String PERMISSIONS = "/permissions";
    public static final String OWNER = "owner";
    public static final String REQUESTER = "requester";
    public static final String VERSION = "/version";
    public static final String CHAT_LINES_LIST = "/chat-lines-list";
    public static final String CHAT_SERVLET = "ChatServlet";
    public static final String WRITE_TO_CHAT = "/write-to-chat";
    public static final String VERSION_PARAM = "version";
}

package constants;

public class Constants {
    public static final String ENGINE = "engine";
    public static final String USERNAME = "username";

    // Servlet Names
    public static final String ENGINE_SERVLET = "EngineServlet";
    public static final String LOGIN_SERVLET = "LoginServlet";
    public static final String SHEET_SERVLET = "SheetServlet";
    public static final String COLOR_SERVLET = "ColorServlet";
    public static final String FILTER_SERVLET = "FilterServlet";
    public static final String SORT_SERVLET = "SortServlet";
    public static final String NO_NAME_RANGE_SERVLET = "NoNameRangeServlet";
    public static final String LOADFILE_SERVLET = "LoadFileServlet";
    public static final String LOGOUT_SERVLET = "LogoutServlet";
    public static final String CELL_SERVLET = "CellServlet";
    public static final String RANGE_SERVLET = "RangeServlet";
    public static final String DYNAMIC_CHANGE_SERVLET = "DynamicChangeServlet";
    public static final String USERS_SERVLET = "UsersServlet";
    public static final String PERMISSION_SERVLET = "PermissionServlet";
    public static final String CHAT_SERVLET = "ChatServlet";

}
 */