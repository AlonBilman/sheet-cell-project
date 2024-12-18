package servlets;

import constants.Constants;
import dto.CellDataDto;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.AppManager;
import manager.impl.SheetManagerImpl;
import utils.ResponseUtils;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;

import static constants.Constants.GSON;
import static constants.Constants.SHEET_ID;

@WebServlet(name = Constants.CELL_SERVLET, urlPatterns = {
        Constants.DISPLAY + Constants.CELL_DTO,
        Constants.MODIFY + Constants.CELL,
        Constants.DYNAMICALLY + Constants.CELL
})

public class CellServlet extends HttpServlet {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        String sheetId = request.getParameter(SHEET_ID);
        String cellId = request.getParameter(Constants.CELL_ID);

        if (sheetId == null || sheetId.isEmpty() || cellId == null || cellId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "One or more parameters are missing (IDs)");
            return;
        }

        lock.readLock().lock();
        try {
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            if (!ServletUtils.isValidEngine(engine, response))
                return;

            CellDataDto cellDataDTO = engine.getCellDto(username, sheetId, cellId);
            ResponseUtils.writeSuccessResponse(response, cellDataDTO);
        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        if (!ServletUtils.isUserNameExists(response, username))
            return;

        String sheetId = request.getParameter(Constants.SHEET_ID);
        String cellId = request.getParameter(Constants.CELL_ID);

        if (sheetId == null || sheetId.isEmpty() || cellId == null || cellId.isEmpty()) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "One or more parameters are missing (IDs)");
            return;
        }

        lock.writeLock().lock();
        try {
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            if (!ServletUtils.isValidEngine(engine, response))
                return;

            AppManager appManager = engine.getManager(username, sheetId);

            if (!appManager.isUpToDate()) {
                throw new RuntimeException("Sheet is not up to date.\n" +
                        "In order to modify it, please update the sheet first.");
            }
            SheetManagerImpl sheetManager = appManager.getSheetManager();

            if (!sheetManager.havePermissionToEdit(username))
                throw new IOException("Permission denied");

            String newOriginalValue = GSON.fromJson(request.getReader(), String.class);
            sheetManager.updateCell(cellId, newOriginalValue, false, username);
            appManager.updateVersion();
            ResponseUtils.writeSuccessResponse(response, null);
        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }
}