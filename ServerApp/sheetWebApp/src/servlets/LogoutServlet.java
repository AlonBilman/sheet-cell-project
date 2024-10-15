package servlets;

import constants.Constants;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.ResponseUtils;

import java.io.IOException;

@WebServlet(name = Constants.LOGOUT_SERVLET, urlPatterns = {Constants.LOGOUT})
public class LogoutServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        synchronized (this) {
            if (!ServletUtils.isUserNameExists(response, username)) {
                return;
            }

            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);

            if (!ServletUtils.isValidEngine(engine, response)) {
                return;
            }

            try {
                engine.removeUser(username);
                ResponseUtils.writeSuccessResponse(response, null);
            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }
}
