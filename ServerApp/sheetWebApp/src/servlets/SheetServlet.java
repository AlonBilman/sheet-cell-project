package servlets;

import constants.Constants;
import dto.sheetDTO;
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

@WebServlet(name = Constants.SHEET_SERVLET, urlPatterns = {Constants.DISPLAY + Constants.SHEET_DTO})
public class SheetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        String sheetId = request.getParameter(SHEET_ID);

        if (sheetId == null || sheetId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No Sheet ID provided");
            return;
        }

        try {
            synchronized (this) {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;
                sheetDTO sheetdto = engine.getSheetDTO(sheetId, username);
                ResponseUtils.writeSuccessResponse(response, sheetdto);
            }

        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
