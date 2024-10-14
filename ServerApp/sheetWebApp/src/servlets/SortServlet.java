package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.CellDataDTO;
import dto.sheetDTO;
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

import static constants.Constants.*;


@WebServlet(name = SORT_SERVLET, urlPatterns = {DISPLAY + SORT})
public class SortServlet extends HttpServlet {

    Gson GSON = new Gson();

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

                ResponseUtils.SortObj sort = GSON.fromJson(request.getReader(), ResponseUtils.SortObj.class);
                SheetManagerImpl sheetManager = engine.getSheetManager(username,sheetId);
                sheetDTO sheet = sheetManager.sort(sort.getParams(),sort.getSortBy());
                ResponseUtils.writeSuccessResponse(response, sheet);
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

}
