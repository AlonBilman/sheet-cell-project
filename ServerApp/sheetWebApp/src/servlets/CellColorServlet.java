package servlets;

import constants.Constants;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.AppManager;
import manager.impl.SheetManagerImpl;
import utils.ResponseUtils;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static constants.Constants.GSON;
import static constants.Constants.SHEET_ID;

@WebServlet(name = Constants.COLOR_SERVLET, urlPatterns = {Constants.MODIFY + Constants.CELL_TEXT_COLOR, Constants.MODIFY + Constants.CELL_BACKGROUND_COLOR,})

public class CellColorServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        String sheetId = request.getParameter(SHEET_ID);
        String cellId = request.getParameter(Constants.CELL_ID);

        if (sheetId == null || sheetId.isEmpty() || cellId == null || cellId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "One or more parameters are missing (IDs)");
            return;
        }
        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;

                AppManager appManager = engine.getManager(username, sheetId);

                if (!appManager.isUpToDate()) {
                    throw new RuntimeException("Sheet is not up to date.\n" +
                            "in order to modify it please update the sheet first.");
                }

                SheetManagerImpl sheetManager = appManager.getSheetManager();

                if (!sheetManager.havePermissionToEdit(username)) {
                    throw new RuntimeException("Permission denied.");
                }

                String color = GSON.fromJson(request.getReader(), String.class);

                if (request.getContextPath().contains(Constants.CELL_TEXT_COLOR)) {
                    sheetManager.setTextColor(cellId, color);
                } else {
                    sheetManager.setBackgroundColor(cellId, color);
                }
                appManager.updateVersion();
                ResponseUtils.writeSuccessResponse(response, null);
            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }
}