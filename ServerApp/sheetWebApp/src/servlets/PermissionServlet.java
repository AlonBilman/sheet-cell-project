package servlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.Engine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.impl.PermissionDecision;
import manager.impl.SheetManagerImpl;
import utils.*;

import java.io.IOException;
import java.util.*;

import static constants.Constants.*;

@WebServlet(name = Constants.PERMISSION_SERVLET, urlPatterns = {Constants.PERMISSIONS})
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

                Map<String, PermissionDecision> permissionFinalizeMap = sheetManager.getFinalizedPermissions();
                Map<String, PermissionDecision> permissionRequestMap = sheetManager.getPendingPermissionsRequests();
                List<PermissionDecision> permissionHistory = sheetManager.getPermissionHistory();

                List<Permission> combinedList = new ArrayList<>();
                combinedList.addAll(extractData(permissionFinalizeMap));
                combinedList.addAll(extractData(permissionRequestMap));

                List<Permission> history = extractData(permissionHistory);
                ResponseUtils.writeSuccessResponse(response, new PermissionData(combinedList, history));


            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    private List<Permission> extractData(Map<String, PermissionDecision> permissionDecisionMap) {
        List<Permission> list = new ArrayList<>();
        for (Map.Entry<String, PermissionDecision> entry : permissionDecisionMap.entrySet()) {
            String user = entry.getKey();
            String permission = entry.getValue().getPermissionStatus().toString();
            String approved = entry.getValue().getApprovalStatus().toString();
            list.add(new Permission(user, permission, approved));
        }
        return list;
    }

    private List<Permission> extractData(List<PermissionDecision> permissionDecisionList) {
        List<Permission> list = new ArrayList<>();
        for (PermissionDecision permissionDecision : permissionDecisionList) {
            String user = permissionDecision.getName();
            String permission = permissionDecision.getPermissionStatus().toString();
            String approved = permissionDecision.getApprovalStatus().toString();
            list.add(new Permission(user, permission, approved));
        }
        return list;
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
                if (sheetManager.isOwner(username)) {
                    throw new IllegalArgumentException("You are already the owner.");
                }

                Engine.PermissionStatus permissionStatus = Engine.PermissionStatus.valueOf(permissionTo.toUpperCase());
                if (permissionStatus.equals(Engine.PermissionStatus.OWNER)) {
                    throw new IllegalArgumentException("You cannot request owner permission.");
                }

                sheetManager.addPermissionRequest(username, permissionStatus);

                ResponseUtils.writeSuccessResponse(response, "submitted");

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
        String permissionResponse = GSON.fromJson(request.getReader(), String.class);

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

                if (!sheetManager.isOwner(username)) {
                    throw new IllegalArgumentException("Only the owner can approve or deny permission requests.");
                }

                PermissionDecision permissionDecision = sheetManager.getPendingPermissionsRequests().get(requester);

                if (permissionDecision == null) {
                    throw new RuntimeException("No permission request found for user: " + requester);
                }

                Engine.ApprovalStatus approvalStatus = permissionResponse.equalsIgnoreCase("yes")
                        ? Engine.ApprovalStatus.YES
                        : Engine.ApprovalStatus.NO;

                if (approvalStatus == Engine.ApprovalStatus.YES) {
                    sheetManager.setPermissionFinalDecision(requester, Engine.ApprovalStatus.YES);
                    engine.addSheetManager(requester, sheetManager, false);
                    ResponseUtils.writeSuccessResponse(response, "Permission approved for user: " + requester);
                } else {
                    sheetManager.setPermissionFinalDecision(requester, Engine.ApprovalStatus.NO);
                    ResponseUtils.writeSuccessResponse(response, "Permission denied for user: " + requester);
                }

            } catch (Exception e) {
                ResponseUtils.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }
}