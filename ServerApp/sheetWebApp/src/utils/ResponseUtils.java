package utils;

import common.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static common.api.path.path.GSON;

public class ResponseUtils {

    //write error response in JSON format
    public static void writeErrorResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        response.setStatus(status);
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, status);
        response.getWriter().write(GSON.toJson(errorResponse));
    }

    //write success response in JSON format
    public static void writeSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        if (data != null)
            response.getWriter().write(GSON.toJson(data));
    }
}
