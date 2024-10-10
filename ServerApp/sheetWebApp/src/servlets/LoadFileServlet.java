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
import utils.SessionUtils;

import java.io.*;
import java.util.Collection;
import java.util.Scanner;

import static constants.Constants.*;

@MultipartConfig
@WebServlet(name = LOADFILE_SERVLET, urlPatterns = {LOADFILE})
public class LoadFileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");
        Gson gson = new Gson();

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Part part = null;
        try {
            part = request.getPart("file");
        } catch (ServletException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(e.getMessage()));
            return;
        }

        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(ENGINE);
                SheetManagerImpl sheetManager = new SheetManagerImpl();
                InputStream fileInputStream = part.getInputStream();
                sheetManager.Load(fileInputStream); //if we couldn't load => don't add the sheetManager (catch)

                engine.addSheetManager(username, sheetManager);
                response.setStatus(HttpServletResponse.SC_OK);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(e.getMessage()));
            }
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}


