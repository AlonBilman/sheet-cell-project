//package servlets;
//
//import com.google.gson.Gson;
//import common.dto.range.RangeBody;
//import common.dto.range.RangesForCharts;
//import constants.Constants;
//import dto.CellDataDTO;
//import engine.Engine;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import manager.impl.AppManager;
//import manager.impl.SheetManagerImpl;
//import utils.ResponseUtils;
//import utils.ServletUtils;
//import utils.SessionUtils;
//
//import java.io.IOException;
//import java.util.Set;
//
//import static common.api.path.path.*;
//
//
//@WebServlet(name = Constants.NO_NAME_RANGE_SERVLET, urlPatterns = {NO_NAME_RANGE, NO_NAME_RANGES})
//public class NoNameRangeServlet extends HttpServlet {
//
//    Gson GSON = new Gson();
//
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String username = SessionUtils.getUsername(request);
//        response.setContentType("application/json");
//
//        if (!ServletUtils.isUserNameExists(response, username)) {
//            return;
//        }
//
//        String sheetId = request.getParameter(SHEET_ID);
//        if (sheetId == null || sheetId.isEmpty()) {
//            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Sheet id is missing");
//            return;
//        }
//
//        try {
//            synchronized (this) {
//                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
//                if (!ServletUtils.isValidEngine(engine, response)) {
//                    return;
//                }
//                AppManager appManager = engine.getManager(username, sheetId);
//
//                if (!appManager.isUpToDate()) {
//                    throw new RuntimeException("In order to use this functionality you have to be updated\n" +
//                            "please update the sheet in order to continue.");
//                }
//
//                SheetManagerImpl sheetManager = appManager.getSheetManager();
//
//                if (request.getServletPath().contains(NO_NAME_RANGES)) {
//                    RangesForCharts rangesForCharts = GSON.fromJson(request.getReader(), RangesForCharts.class);
//
//                    if (rangesForCharts == null) {
//                        throw new IllegalArgumentException("Invalid range data");
//                    }
//                    Set<CellDataDTO> xRange = sheetManager.getSetOfCellsDtoDummyRange(rangesForCharts.getXParams());
//                    Set<CellDataDTO> yRange = sheetManager.getSetOfCellsDtoDummyRange(rangesForCharts.getYParams());
//                    rangesForCharts.setXRange(xRange);
//                    rangesForCharts.setYRange(yRange);
//                    ResponseUtils.writeSuccessResponse(response, rangesForCharts);
//                } else {
//                    RangeBody rangeBody = GSON.fromJson(request.getReader(), RangeBody.class);
//
//                    if (rangeBody == null || rangeBody.getToAndFrom() == null) {
//                        throw new IllegalArgumentException("Invalid range data");
//                    }
//
//                    Set<CellDataDTO> range = sheetManager.getSetOfCellsDtoDummyRange(rangeBody.getToAndFrom());
//                    ResponseUtils.writeSuccessResponse(response, range);
//                }
//            }
//
//        } catch (Exception e) {
//            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
//        }
//    }
//}
