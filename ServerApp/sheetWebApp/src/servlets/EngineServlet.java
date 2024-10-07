package servlets;

import engine.impl.EngineImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import static constants.constants.ENGINE;
import static constants.constants.ENGINE_SERVLET;

@WebServlet(name = ENGINE_SERVLET, loadOnStartup = 1)  // Eager initialization
public class EngineServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        try {
            // Initialize the engine
            EngineImpl engine = new EngineImpl();

            // Store the engine instance in the ServletContext
            ServletContext context = getServletContext();
            context.setAttribute(ENGINE, engine);

        } catch (Exception e) {
            // Handle any exceptions during initialization
            throw new ServletException("Failed to initialize Engine", e);
        }
    }
}