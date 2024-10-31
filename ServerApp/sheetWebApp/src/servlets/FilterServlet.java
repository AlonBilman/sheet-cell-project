package servlets;

import constants.Constants;
import dto.sheetDTO;
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

import static constants.Constants.*;

@WebServlet(name = FILTER_SERVLET, urlPatterns = {DISPLAY + FILTER})
public class FilterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        String sheetId = request.getParameter(SHEET_ID);

        if (sheetId == null || sheetId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "sheet id is missing");
            return;
        }
        try {
            synchronized (this) {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;
                ResponseUtils.FilterObj filterObj = GSON.fromJson(request.getReader(), ResponseUtils.FilterObj.class);
                AppManager appManager = engine.getManager(username, sheetId);
                if (!appManager.isUpToDate()) {
                    throw new RuntimeException("In order to use filter functionality you have to update the sheet.");
                }
                SheetManagerImpl sheetManager = appManager.getSheetManager();
                sheetDTO sheet;
                if (filterObj.getOperator().equals("OR"))
                    sheet = sheetManager.filter(filterObj.getParams(), filterObj.getFilterBy(), SheetManagerImpl.OperatorValue.OR_OPERATOR);
                else
                    sheet = sheetManager.filter(filterObj.getParams(), filterObj.getFilterBy(), SheetManagerImpl.OperatorValue.AND_OPERATOR);
                ResponseUtils.writeSuccessResponse(response, sheet);
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
