package servlets;

import constants.Constants;
import dto.SheetDto;
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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static constants.Constants.*;

@WebServlet(name = DYNAMIC_CHANGE_SERVLET, urlPatterns = DISPLAY + DYNAMIC_CHANGE)
public class DynamicChangeServlet extends HttpServlet {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

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

            SheetManagerImpl sheetManager = engine.getSheetManagerCopy(username, sheetId);
            String newOriginalValue = GSON.fromJson(request.getReader(), String.class);
            SheetDto sheetDto = sheetManager.setOriginalValDynamically(cellId, newOriginalValue);
            ResponseUtils.writeSuccessResponse(response, sheetDto);
        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

            SheetManagerImpl sheetManager = engine.getSheetManagerCopy(username, sheetId);
            SheetDto sheetDto = sheetManager.finishedDynamicallyChangeFeature(cellId);
            engine.getManager(username, sheetId).dynamicChangeStopped();
            ResponseUtils.writeSuccessResponse(response, sheetDto);
        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

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

        lock.readLock().lock();
        try {
            Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
            if (!ServletUtils.isValidEngine(engine, response))
                return;

            AppManager appManager = engine.getManager(username, sheetId);
            if (!appManager.isUpToDate()) {
                throw new RuntimeException("In order to use dynamic-change functionality you have to update the sheet.");
            }

            SheetManagerImpl sheetManager = appManager.getManagerDeepCopyForDynamicChange();
            sheetManager.saveCellValue(cellId);
            ResponseUtils.writeSuccessResponse(response, null);
        } catch (Exception e) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }
}
