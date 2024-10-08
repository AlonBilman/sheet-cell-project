package servlets;

import com.google.gson.Gson;
import constants.constants;
import dto.sheetDTO;
import manager.impl.SheetManagerImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = constants.SHEET_SERVLET, urlPatterns = {constants.DISPLAY+constants.SHEET_DTO})
public class SheetServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        try {
            synchronized (this) {

                SheetManagerImpl engine = (SheetManagerImpl) getServletContext().getAttribute(constants.ENGINE);

                if (engine == null) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);//500
                    response.getWriter().write(gson.toJson("Server engine not initialized"));
                    return;
                }
                sheetDTO sheet = engine.Display();
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(sheet));

            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson("Invalid JSON format"));
        }
    }

}
