package servlets;

import engine.Engine;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import static constants.constants.*;

@WebServlet(name = ENGINE_SERVLET, urlPatterns = {START_UP}, loadOnStartup = 1)  // Eager initialization
public class EngineServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        try {
            Engine engine = new Engine();
            ServletContext context = getServletContext();
            context.setAttribute(ENGINE, engine);
        } catch (Exception e) {
            throw new ServletException("Failed to initialize SheetManager", e);
        }
    }
}