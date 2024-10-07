package servlets;

import com.google.gson.Gson;
import constants.*;
import engine.impl.EngineImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

import static constants.constants.ENGINE;

@WebServlet(name = constants.LOGIN_SERVLET, urlPatterns = {constants.LOGIN})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        String line;

        // Read JSON string from the request body
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String jsonData = sb.toString();
        try {
            // Deserialize the JSON request body to a string (username)
            String username = gson.fromJson(jsonData, String.class);

            if (username == null || username.trim().isEmpty()) {
                // Handle invalid username
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson("Invalid username format"));
                return;
            }

            synchronized (this) {
                // Get the engine from the ServletContext
                EngineImpl engine = (EngineImpl) getServletContext().getAttribute(ENGINE);

                if (engine == null) {
                    // If engine is not initialized, return an error
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson("Server engine not initialized"));
                    return;
                }

                // Check if the user already exists
                if (engine.isUserExists(username)) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict if username already exists
                    resp.getWriter().write(gson.toJson("A user with this name already exists"));
                } else {
                    // Add the new user to the engine
                    engine.addUser(username);
                    resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created for successful user creation
                    resp.getWriter().write(gson.toJson("User created successfully"));
                }
            }

        } catch (Exception e) {
            // Handle invalid JSON format
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("Invalid JSON format"));
        }
    }
}
