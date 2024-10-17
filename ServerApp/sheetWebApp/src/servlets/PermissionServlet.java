package servlets;

import constants.Constants;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.SheetManagerImpl;
import utils.*;

import java.io.IOException;
import java.util.*;

import static constants.Constants.OWNER;
import static constants.Constants.SHEET_ID;

@WebServlet(name = Constants.PERMISSION_SERVLET, urlPatterns = {Constants.DISPLAY + Constants.PERMISSIONS})
public class PermissionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        String sheetName = request.getParameter(SHEET_ID);
        String owner = request.getParameter(OWNER);

        if (!ServletUtils.isUserNameExists(response, username))
            return;
        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;
                SheetManagerImpl sheetManager = engine.getSheetManager(owner, sheetName);
                Map<String, AbstractMap.SimpleEntry<Engine.PermissionStatus, Boolean>> permissionStatusMap =
                        sheetManager.getPermissionStatusMap();
                List<PermissionData> list = new ArrayList<>();
                for (Map.Entry<String, AbstractMap.SimpleEntry<Engine.PermissionStatus, Boolean>> entry : permissionStatusMap.entrySet()) {
                    String user = entry.getKey();
                    String permission = entry.getValue().getKey().toString();
                    boolean approved = entry.getValue().getValue();
                    PermissionData dataToAdd = new PermissionData(user, permission, approved);
                    list.add(dataToAdd);
                    ResponseUtils.writeSuccessResponse(response, list);
                }

            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }
}

