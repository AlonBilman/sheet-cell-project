package servlets;

import engine.impl.EngineImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "EngineServlet", urlPatterns = {"/"}, loadOnStartup = 1)  // Eager initialization
public class EngineServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Initialize the engine
        EngineImpl engine = new EngineImpl();

        // Store the engine instance in the ServletContext
        ServletContext context = getServletContext();
        context.setAttribute("engine", engine);

        // Use the engine for processing...
        response.getWriter().println("Engine instance retrieved successfully.");
    }
}