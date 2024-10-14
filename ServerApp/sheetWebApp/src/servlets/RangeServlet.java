package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = Constants.RANGE_SERVLET, urlPatterns = {Constants.RANGE})
public class RangeServlet extends HttpServlet {

    Gson gson = new Gson();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {

    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {

    }
}
