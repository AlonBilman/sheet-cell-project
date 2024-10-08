package servlets;

import com.google.gson.Gson;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.SheetManagerImpl;
import utils.SessionUtils;

import java.io.*;

import static constants.constants.*;


@WebServlet(name = LOADFILE_SERVLET, urlPatterns = {LOADFILE})
public class LoadFileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");
        Gson gson = new Gson();

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(ENGINE);
                SheetManagerImpl sheetManager = new SheetManagerImpl();
                InputStream fileInputStream = request.getInputStream();
                sheetManager.Load(fileInputStream);
                engine.addSheetManager(username, sheetManager);
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(e.getMessage()));
            }
        }
    }


}


