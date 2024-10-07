package servlets;
import constants.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = constants.LOGIN_SERVLET, urlPatterns = {constants.LOGIN})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set response content type
        resp.setStatus(HttpServletResponse.SC_OK);
//        // Initialize Gson
//        Gson gson = new Gson();
//        StringBuilder sb = new StringBuilder();
//        String line;
//
//        // Read JSON string from the request body
//        try (BufferedReader reader = req.getReader()) {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//        }
//
//        String jsonData = sb.toString();
//        try {
//            // Extract username from JSON
//            String username = gson.fromJson(jsonData, String.class);
//
//            // Handle adding the user (e.g., save to the database)
//            // For now, just return a success message
//
//            resp.setStatus(HttpServletResponse.SC_CREATED);
//            resp.getWriter().write(gson.toJson("User added: " + username));
//
//        } catch (JsonSyntaxException e) {
//            // Handle invalid JSON format
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            resp.getWriter().write(gson.toJson("Invalid JSON format"));
//        }
   }
}
