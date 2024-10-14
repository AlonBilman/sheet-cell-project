package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.CellDataDTO;
import dto.RangeDTO;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.SheetManagerImpl;
import utils.ResponseUtils;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = Constants.RANGE_SERVLET, urlPatterns = {Constants.RANGE})
public class RangeServlet extends HttpServlet {

    Gson GSON = new Gson();


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username)) {
            return;
        }

        String sheetId = request.getParameter(Constants.SHEET_ID);
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

                ResponseUtils.RangeBody rangeBody = GSON.fromJson(request.getReader(), ResponseUtils.RangeBody.class);

                if (rangeBody == null || rangeBody.getName() == null || rangeBody.getToAndFrom() == null) {
                    ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid range data");
                    return;
                }

                SheetManagerImpl sheetManager = engine.getSheetManager(username, sheetId);
                sheetManager.addRange(rangeBody.getName(), rangeBody.getToAndFrom());

                ResponseUtils.writeSuccessResponse(response, null);
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username)) {
            return;
        }

        String sheetId = request.getParameter(Constants.SHEET_ID);
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
                ResponseUtils.RangeBody rangeBody = GSON.fromJson(request.getReader(), ResponseUtils.RangeBody.class);

                if (rangeBody == null || rangeBody.getName() == null || rangeBody.getToAndFrom() == null) {
                    ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid range data");
                    return;
                }

                SheetManagerImpl sheetManager = engine.getSheetManager(username, sheetId);
                sheetManager.deleteRange(rangeBody.getName());

                ResponseUtils.writeSuccessResponse(response, null);
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
