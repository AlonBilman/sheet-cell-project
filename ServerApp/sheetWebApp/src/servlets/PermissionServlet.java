package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.SheetManagerImpl;
import utils.*;

import java.io.IOException;
import java.util.*;

import static constants.Constants.*;


@WebServlet(name = Constants.PERMISSION_SERVLET, urlPatterns = {Constants.PERMISSIONS,
        Constants.PERMISSION_REQUESTS})
public class PermissionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        String sheetName = request.getParameter(SHEET_ID);
        String owner = request.getParameter(OWNER);

        if (!ServletUtils.isUserNameExists(response, username))
            return;
        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response))
                    return;
                SheetManagerImpl sheetManager = engine.getSheetManager(owner, sheetName);
                Map<String, AbstractMap.SimpleEntry<Engine.PermissionStatus, Engine.ApprovalStatus>> permissionStatusMap =
                        sheetManager.getPermissionStatusMap();
                List<PermissionData> list = new ArrayList<>();
                boolean bool = request.getServletPath().contains(PERMISSION_REQUESTS);
                for (Map.Entry<String, AbstractMap.SimpleEntry<Engine.PermissionStatus, Engine.ApprovalStatus>> entry : permissionStatusMap.entrySet()) {
                    String user = entry.getKey();
                    String permission = entry.getValue().getKey().toString();
                    String approved = entry.getValue().getValue().toString();
                    if (bool) {
                        if (approved.equals(Engine.ApprovalStatus.PENDING.toString())) {
                            list.add(new PermissionData(user, permission, approved));
                        }
                    } else {
                        list.add(new PermissionData(user, permission, approved));
                    }
                }
                ResponseUtils.writeSuccessResponse(response, list);

            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson GSON = new Gson();
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        String sheetName = request.getParameter(SHEET_ID);
        String owner = request.getParameter(OWNER);
        String permissionTo = GSON.fromJson(request.getReader(), String.class);

        if (sheetName == null || sheetName.isEmpty() || owner == null || owner.isEmpty() || permissionTo == null || permissionTo.isEmpty()) {
            throw new IOException("One or more parameters are missing");
        }

        if (!ServletUtils.isUserNameExists(response, username)) {
            return;
        }

        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response)) {
                    return;
                }

                SheetManagerImpl sheetManager = engine.getSheetManager(owner, sheetName);
                Map<String, AbstractMap.SimpleEntry<Engine.PermissionStatus, Engine.ApprovalStatus>> permissionStatusMap =
                        sheetManager.getPermissionStatusMap();

                AbstractMap.SimpleEntry<Engine.PermissionStatus, Engine.ApprovalStatus> currentStatus = permissionStatusMap.get(username);
                if (currentStatus != null && currentStatus.getKey().equals(Engine.PermissionStatus.OWNER)) {
                    throw new IllegalArgumentException("You are already the owner.");
                }

                Engine.PermissionStatus permissionStatus = Engine.PermissionStatus.valueOf(permissionTo.toUpperCase());
                if (permissionStatus.equals(Engine.PermissionStatus.OWNER)) {
                    throw new IllegalArgumentException("You cannot request owner permission.");
                }

                AbstractMap.SimpleEntry<Engine.PermissionStatus, Engine.ApprovalStatus> approvalStatus =
                        new AbstractMap.SimpleEntry<>(permissionStatus, Engine.ApprovalStatus.PENDING);

                permissionStatusMap.put(username, approvalStatus);

                ResponseUtils.writeSuccessResponse(response, "submitted");

            } catch (IllegalArgumentException e) {
                //case where the input string does not match enum constant or invalid requests.
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson GSON = new Gson();
        String username = SessionUtils.getUsername(request);
        response.setContentType("application/json");

        String sheetName = request.getParameter(SHEET_ID);
        String requester = request.getParameter(REQUESTER);

        String permissionResponse = GSON.fromJson(request.getReader(),String.class);

        if (!permissionResponse.equalsIgnoreCase("yes") && !permissionResponse.equalsIgnoreCase("no")) {
            ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid response. Expected 'yes' or 'no'.");
            return;
        }

        if (sheetName == null || sheetName.isEmpty() || requester == null || requester.isEmpty()) {
            throw new IOException("One or more parameters are missing");
        }

        if (!ServletUtils.isUserNameExists(response, username)) {
            return;
        }

        synchronized (this) {
            try {
                Engine engine = (Engine) getServletContext().getAttribute(Constants.ENGINE);
                if (!ServletUtils.isValidEngine(engine, response)) {
                    return;
                }

                SheetManagerImpl sheetManager = engine.getSheetManager(username, sheetName);
                Map<String, AbstractMap.SimpleEntry<Engine.PermissionStatus, Engine.ApprovalStatus>> permissionStatusMap =
                        sheetManager.getPermissionStatusMap();

                AbstractMap.SimpleEntry<Engine.PermissionStatus, Engine.ApprovalStatus> currentStatus = permissionStatusMap.get(requester);

                if (currentStatus == null) {
                    ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No permission request found for user: " + username);
                    return;
                }
                //set approval status to yes
                if (permissionResponse.equalsIgnoreCase("yes")) {
                    Engine.PermissionStatus permissionStatus = currentStatus.getKey();
                    AbstractMap.SimpleEntry<Engine.PermissionStatus, Engine.ApprovalStatus> approvalStatus =
                            new AbstractMap.SimpleEntry<>(permissionStatus, Engine.ApprovalStatus.YES);

                    permissionStatusMap.put(requester, approvalStatus);
                    engine.addSheetManager(requester,sheetManager,false);
                    ResponseUtils.writeSuccessResponse(response, "Permission approved for user: " + username);
                } else {
                    Engine.PermissionStatus permissionStatus = currentStatus.getKey();
                    AbstractMap.SimpleEntry<Engine.PermissionStatus, Engine.ApprovalStatus> approvalStatus =
                            new AbstractMap.SimpleEntry<>(permissionStatus, Engine.ApprovalStatus.NO);

                    permissionStatusMap.put(requester, approvalStatus);
                    ResponseUtils.writeSuccessResponse(response, "Permission denied for user: " + username);
                }

            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }
}