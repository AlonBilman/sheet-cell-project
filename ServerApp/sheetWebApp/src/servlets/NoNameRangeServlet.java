package servlets;

import constants.Constants;
import dto.CellDataDto;
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
import java.util.Set;

import static constants.Constants.*;

@WebServlet(name = Constants.NO_NAME_RANGE_SERVLET, urlPatterns = {NO_NAME_RANGE, Constants.NO_NAME_RANGES})
public class NoNameRangeServlet extends HttpServlet {


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
                AppManager appManager = engine.getManager(username, sheetId);

                if (!appManager.isUpToDate()) {
                    throw new RuntimeException("In order to use this functionality you have to be updated\n" +
                            "please update the sheet in order to continue.");
                }

                SheetManagerImpl sheetManager = appManager.getSheetManager();

                if (request.getServletPath().contains(NO_NAME_RANGES)) {
                    ResponseUtils.Ranges ranges = GSON.fromJson(request.getReader(), ResponseUtils.Ranges.class);

                    if (ranges == null) {
                        throw new IllegalArgumentException("Invalid range data");
                    }
                    Set<CellDataDto> xRange = sheetManager.getSetOfCellsDtoDummyRange(ranges.getXParams());
                    Set<CellDataDto> yRange = sheetManager.getSetOfCellsDtoDummyRange(ranges.getYParams());
                    ranges.setXRange(xRange);
                    ranges.setYRange(yRange);
                    ResponseUtils.writeSuccessResponse(response, ranges);
                } else {
                    ResponseUtils.RangeBody rangeBody = GSON.fromJson(request.getReader(), ResponseUtils.RangeBody.class);

                    if (rangeBody == null || rangeBody.getToAndFrom() == null) {
                        throw new IllegalArgumentException("Invalid range data");
                    }

                    Set<CellDataDto> range = sheetManager.getSetOfCellsDtoDummyRange(rangeBody.getToAndFrom());
                    ResponseUtils.writeSuccessResponse(response, range);
                }
            }
        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
