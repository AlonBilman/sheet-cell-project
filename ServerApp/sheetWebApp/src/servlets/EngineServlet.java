package servlets;

import engine.impl.EngineImpl;
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
            EngineImpl engine = new EngineImpl();
            ServletContext context = getServletContext();
            context.setAttribute(ENGINE, engine);
        } catch (Exception e) {
            throw new ServletException("Failed to initialize Engine", e);
        }
    }
}