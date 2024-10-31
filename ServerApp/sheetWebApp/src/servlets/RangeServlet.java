package servlets;

import common.dto.range.RangeBody;
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

import static common.api.path.path.*;

@WebServlet(name = Constants.RANGE_SERVLET, urlPatterns = {RANGE})
public class RangeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleRangeOperation(request, response, true);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleRangeOperation(request, response, false);
    }

    private void handleRangeOperation(HttpServletRequest request, HttpServletResponse response, boolean isAddOperation) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username)) {
            return;
        }

        String sheetId = request.getParameter(SHEET_ID);
        if (sheetId == null || sheetId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Sheet id is missing");
            return;
        }

        try {
            synchronized (this) {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response)) {
                    return;
                }

                RangeBody rangeBody = GSON.fromJson(request.getReader(), RangeBody.class);

                if (rangeBody == null || rangeBody.getName() == null || rangeBody.getToAndFrom() == null) {
                    ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid range data");
                    return;
                }

                AppManager appManager = engine.getManager(username, sheetId);

                if (!appManager.isUpToDate()) {
                    throw new RuntimeException("In order to use ranges functionality you have to be updated\n" +
                            "please update the sheet in order to continue.");
                }

                SheetManagerImpl sheetManager = appManager.getSheetManager();
                if (!sheetManager.havePermissionToEdit(username)) {
                    throw new IOException("Permission denied");
                }

                if (isAddOperation) {
                    sheetManager.addRange(rangeBody.getName(), rangeBody.getToAndFrom());
                    ResponseUtils.writeSuccessResponse(response, sheetManager.getRangeDto(rangeBody.getName()));
                } else {
                    sheetManager.deleteRange(rangeBody.getName());
                    ResponseUtils.writeSuccessResponse(response, null);
                }
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
