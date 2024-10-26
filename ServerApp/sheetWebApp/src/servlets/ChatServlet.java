package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.ChatManager;
import utils.ResponseUtils;
import utils.ServletUtils;
import utils.SessionUtils;
import manager.impl.SingleChatEntry;

import java.io.IOException;
import java.util.List;

import static constants.Constants.*;

@WebServlet(name = Constants.CHAT_SERVLET, urlPatterns = {CHAT_LINES_LIST, WRITE_TO_CHAT})
public class ChatServlet extends HttpServlet {

    private static final Gson GSON = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        String chatVersion = request.getParameter(VERSION);
        if (chatVersion == null || chatVersion.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Chat Version is missing.");
            return;
        }
        int version;
        try {
            version = Integer.parseInt(chatVersion);
        } catch (NumberFormatException e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        int chatManagerVersion = 0;
        List<SingleChatEntry> chatEntries;

        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;
                ChatManager chatManager = engine.getChatManager();
                chatEntries = chatManager.getChatEntries(version);
                ResponseUtils.writeSuccessResponse(response, new ChatAndVersion(chatEntries, chatManagerVersion));
            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        String textTyped = GSON.fromJson(request.getReader(), String.class);

        if (textTyped == null || textTyped.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Message can not be empty.");
            return;
        }

        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;
                ChatManager chatManager = engine.getChatManager();
                chatManager.addChatString(textTyped, username);
                ResponseUtils.writeSuccessResponse(response, null);
            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    private record ChatAndVersion(List<SingleChatEntry> entries, int version) {
    }
}