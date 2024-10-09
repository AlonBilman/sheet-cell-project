package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;


@WebServlet(name = Constants.LOGOUT_SERVLET, urlPatterns = {Constants.LOGOUT})
public class LogoutServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");
        Gson gson = new Gson();
        synchronized (this) {
            if (!ServletUtils.isUserNameExists(response, username))
                return;

            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            if (!ServletUtils.isValidEngine(engine, response))
                return;
            try {
                engine.removeUser(username);
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(e.getMessage()));
            }
        }
    }
}
