package manager.impl;

import engine.Engine;

import java.util.HashMap;
import java.util.Map;

public class PermissionManager {
    private final Map<String, PermissionDecision> finalizedPermissions;
    private final Map<String, PermissionDecision> pendingRequests;

    public Map<String, PermissionDecision> getFinalizedPermissions() {
        return finalizedPermissions;
    }

    public Map<String, PermissionDecision> getPendingRequests() {
        return pendingRequests;
    }

    boolean havePermissionToEdit(String user) {
        PermissionDecision permissionDecision = finalizedPermissions.get(user);
        if (permissionDecision != null) {
            return (!permissionDecision.getPermissionStatus().equals(Engine.PermissionStatus.READER) &&
                    permissionDecision.getApprovalStatus().equals(Engine.ApprovalStatus.YES));
        }
        return false;
    }

    boolean isOwner(String user) {
        PermissionDecision permissionDecision = finalizedPermissions.get(user);
        if (permissionDecision != null) {
            return permissionDecision.getPermissionStatus().equals(Engine.PermissionStatus.OWNER);
        }
        return false;
    }

    public PermissionManager() {
        finalizedPermissions = new HashMap<>();
        pendingRequests = new HashMap<>();
    }

    public void addOwner(String owner) {
        addFinalizePermissions(owner, new PermissionDecision(Engine.PermissionStatus.OWNER, Engine.ApprovalStatus.YES));
    }

    private void addFinalizePermissions(String user, PermissionDecision decision) {
        finalizedPermissions.put(user, decision);
    }

    public void addPendingRequest(String request , Engine.PermissionStatus approvalStatus) {
        addPending(request,new PermissionDecision(approvalStatus, Engine.ApprovalStatus.PENDING));
    }

    private void addPending(String user, PermissionDecision decision) {
        pendingRequests.put(user, decision);
    }

    private void removePendingRequest(String user) {
        pendingRequests.remove(user);
    }

    public void setFinalDecision(String user, Engine.ApprovalStatus approvalStatus) {
        PermissionDecision permissionDecision = pendingRequests.get(user);
        if (permissionDecision != null) {
            permissionDecision.setApprovalStatus(approvalStatus);
            addFinalizePermissions(user, permissionDecision);
            removePendingRequest(user);
        }
        else
            throw new RuntimeException("Request was not found. Could not finalize decision for user " + user);
    }
}
