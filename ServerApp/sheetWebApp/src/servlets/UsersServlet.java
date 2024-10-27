package servlets;

import constants.Constants;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.AppManager;
import utils.AppUser;
import utils.ResponseUtils;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet(name = Constants.USERS_SERVLET, urlPatterns = {Constants.DISPLAY + Constants.USERS})
public class UsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        try {
            synchronized (this) {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;

                Map<String, Set<AppManager>> userMap = engine.getUserMap();
                List<AppUser> list = new ArrayList<>();

                for (Map.Entry<String, Set<AppManager>> entry : userMap.entrySet()) {
                    Set<AppManager> appManagers = entry.getValue();
                    for (AppManager appManager : appManagers) {
                        if (appManager.getSheetManager().isOwner(entry.getKey())) {
                            AppUser user = new AppUser(entry.getKey(), appManager.getSheetManager().getSheetName(), appManager.getSheetManager().getSheetSize());
                            list.add(user);
                        }
                    }
                }

                ResponseUtils.writeSuccessResponse(response, list);
            }

        } catch (IllegalArgumentException e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
