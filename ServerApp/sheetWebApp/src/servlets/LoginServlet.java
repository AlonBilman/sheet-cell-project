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

@WebServlet(name = constants.LOGIN_SERVLET, urlPatterns = {constants.LOGIN})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        String line;

        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String jsonData = sb.toString();
        try {
            //get the name
            String username = gson.fromJson(jsonData, String.class);

            if (username == null || username.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400
                resp.getWriter().write(gson.toJson("Invalid username format"));
                return;
            }

            synchronized (this) {

                EngineImpl engine = (EngineImpl) getServletContext().getAttribute(constants.ENGINE);

                if (engine == null) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
                    resp.getWriter().write(gson.toJson("Server engine not initialized"));
                    return;
                }

                if (engine.isUserExists(username)) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT); //409
                    resp.getWriter().write(gson.toJson("A user with this name already exists"));
                } else {
                    engine.addUser(username);
                    resp.setStatus(HttpServletResponse.SC_CREATED); //201
                }
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson("Invalid JSON format"));
        }
    }
}
