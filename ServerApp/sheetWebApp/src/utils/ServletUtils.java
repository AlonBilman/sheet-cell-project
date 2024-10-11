package utils;

import engine.Engine;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletUtils {

    public static boolean isValidEngine(Engine engine, HttpServletResponse response) throws IOException {
        if (engine == null) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server engine not initialized");
            return false;
        }
        return true;
    }

    public static boolean isUserNameExists(HttpServletResponse response, String userName) throws IOException {
        if (userName == null) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "No Username - Unauthorized");
            return false;
        }
        return true;
    }
}
