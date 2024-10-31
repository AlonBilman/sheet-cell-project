package servlets;

import constants.Constants;
import dto.CellDataDTO;
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

import static common.api.path.path.*;

@WebServlet(name = Constants.CELL_SERVLET, urlPatterns = {
        DISPLAY + CELL_DTO,
        MODIFY + CELL,
        DYNAMICALLY + CELL
})

public class CellServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        String sheetId = request.getParameter(SHEET_ID);
        String cellId = request.getParameter(CELL_ID);

        if (sheetId == null || sheetId.isEmpty() || cellId == null || cellId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "One or more parameters are missing (IDs)");
            return;
        }

        try {
            synchronized (this) {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;

                CellDataDTO cellDataDTO = engine.getCellDTO(username, sheetId, cellId);
                ResponseUtils.writeSuccessResponse(response, cellDataDTO);
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        String sheetId = request.getParameter(SHEET_ID);
        String cellId = request.getParameter(CELL_ID);

        if (sheetId == null || sheetId.isEmpty() || cellId == null || cellId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "One or more parameters are missing (IDs)");
            return;
        }
        try {
            synchronized (this) {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;

                AppManager appManager = engine.getManager(username, sheetId);

                if (!appManager.isUpToDate()) {
                    throw new RuntimeException("Sheet is not up to date.\n" +
                            "in order to modify it please update the sheet first.");
                }

                SheetManagerImpl sheetManager = appManager.getSheetManager();

                if (!sheetManager.havePermissionToEdit(username))
                    throw new IOException("Permission denied");

                String newOriginalValue = GSON.fromJson(request.getReader(), String.class);
                sheetManager.updateCell(cellId, newOriginalValue, false, username);
                appManager.updateVersion();
                ResponseUtils.writeSuccessResponse(response, null);
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
