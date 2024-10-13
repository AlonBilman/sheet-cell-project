package utils;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtils {

    private static final Gson GSON = new Gson();

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

    private static class ErrorResponse {
        private final String error;
        private final int status;

        public ErrorResponse(String error, int status) {
            this.error = error;
            this.status = status;
        }

        public ErrorResponse() {
            this.error = null;
            this.status = 0;
        }

        public String getError() {
            return error;
        }

        public int getStatus() {
            return status;
        }
    }
}
