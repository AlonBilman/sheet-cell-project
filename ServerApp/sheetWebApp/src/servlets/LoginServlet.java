package servlets;

import constants.*;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ResponseUtils;
import utils.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;

import static constants.Constants.GSON;

@WebServlet(name = Constants.LOGIN_SERVLET, urlPatterns = {Constants.LOGIN})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        String jsonData = stringBuilder.toString();
        try {
            String username = GSON.fromJson(jsonData, String.class);

            if (username == null || username.trim().isEmpty()) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid username format");
                return;
            }

            synchronized (this) {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

                if (!ServletUtils.isValidEngine(engine, response)) {
                    return;
                }

                if (engine.isUserExists(username)) {
                    ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_CONFLICT, "A user with this name already exists");
                } else {
                    engine.addUser(username);
                    ResponseUtils.writeSuccessResponse(response, null);
                    request.getSession(true).setAttribute(Constants.USERNAME, username);
                }
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
        }
    }
}
