package utils;

import com.google.gson.Gson;
import engine.Engine;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletUtils {

    public static boolean isValidEngine(Engine engine, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        if(engine == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
            response.getWriter().write(gson.toJson("Server engine not initialized"));
            return false;
        }
        return true;
    }

    public static boolean isUserNameExists(HttpServletResponse response , String userName) throws IOException {
        if (userName == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
