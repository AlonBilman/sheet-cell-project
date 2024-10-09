package servlets;

import com.google.gson.Gson;
import constants.*;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = Constants.LOGIN_SERVLET, urlPatterns = {Constants.LOGIN})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        String jsonData = stringBuilder.toString();
        try {
            //get the name
            String username = gson.fromJson(jsonData, String.class);

            if (username == null || username.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
                response.getWriter().write(gson.toJson("Invalid username format"));
                return;
            }

            synchronized (this) {

                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

                if (!ServletUtils.isValidEngine(engine, response))
                    return;

                if (engine.isUserExists(username)) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT); //409
                    response.getWriter().write(gson.toJson("A user with this name already exists"));
                } else {
                    engine.addUser(username);
                    response.setStatus(HttpServletResponse.SC_CREATED); //201
                    request.getSession(true).setAttribute(Constants.USERNAME, username);
                }
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("Invalid JSON format"));
        }
    }
}
