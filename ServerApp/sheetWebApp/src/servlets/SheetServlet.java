package servlets;

import constants.Constants;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.AppManager;
import utils.ResponseUtils;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static constants.Constants.SHEET_ID;

@WebServlet(name = Constants.SHEET_SERVLET, urlPatterns = {Constants.VERSION, Constants.DISPLAY + Constants.SHEET_DTO, Constants.DISPLAY + Constants.ALL_VERSIONS})
public class SheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String servletPath = request.getServletPath();
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username)) return;

        String sheetId = request.getParameter(SHEET_ID);

        if (sheetId == null || sheetId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No Sheet ID provided");
            return;
        }

        try {
            synchronized (this) {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response)) return;

                Object responseData;
                AppManager appManager = engine.getManager(username, sheetId);
                if (servletPath.contains(Constants.ALL_VERSIONS)) {
                    responseData = appManager.getSheetManager().getSheets(); //Map<Integer,sheetDTO>
                } else if (servletPath.contains(Constants.VERSION)) {
                    responseData = appManager.getSheetManager().getSheetVersion(); //int
                } else {
                    appManager.updateVersion();
                    responseData = engine.getSheetDTO(sheetId, username); //sheetDTO
                }
                ResponseUtils.writeSuccessResponse(response, responseData);
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}