package servlets;

import com.google.gson.Gson;
import constants.Constants;
import dto.CellDataDTO;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ResponseUtils;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static constants.Constants.SHEET_ID;

@WebServlet(name = Constants.CELL_SERVLET, urlPatterns = {Constants.DISPLAY + Constants.CELL_DTO})
public class CellServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        Gson gson = new Gson();
        String sheetId = request.getParameter(SHEET_ID);
        String cellId = request.getParameter(Constants.CELL_ID);

        if (sheetId == null || sheetId.isEmpty() || cellId == null || cellId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "One or more parameters are missing (IDs)");
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
}
