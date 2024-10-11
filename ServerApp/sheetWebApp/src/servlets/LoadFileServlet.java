package servlets;

import com.google.gson.Gson;
import engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import manager.impl.SheetManagerImpl;
import utils.ResponseUtils;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;

import static constants.Constants.*;

@MultipartConfig
@WebServlet(name = LOADFILE_SERVLET, urlPatterns = {LOADFILE})
public class LoadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        Part part;
        try {
            part = request.getPart("file");
        } catch (ServletException e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to retrieve the file part");
            return;
        }

        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(ENGINE);
                SheetManagerImpl sheetManager = new SheetManagerImpl();
                InputStream fileInputStream = part.getInputStream();
                sheetManager.Load(fileInputStream);
                engine.addSheetManager(username, sheetManager);
                ResponseUtils.writeSuccessResponse(response, null);
            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }
}
