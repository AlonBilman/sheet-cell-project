package manager.impl;

import engine.Engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionManager implements Serializable {
    private final Map<String, PermissionDecision> finalizedPermissions;
    private final Map<String, PermissionDecision> pendingRequests;
    private final List<PermissionDecision> permissionHistory;

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
        permissionHistory = new ArrayList<>();
    }

    public void addOwner(String owner) {
        addFinalizePermissions(owner, new PermissionDecision(Engine.PermissionStatus.OWNER, Engine.ApprovalStatus.YES, owner));
    }

    private void addFinalizePermissions(String user, PermissionDecision decision) {
        finalizedPermissions.put(user, decision);
    }

    public void addPendingRequest(String requestedBy, Engine.PermissionStatus approvalStatus) {
        addPending(requestedBy, new PermissionDecision(approvalStatus, Engine.ApprovalStatus.PENDING, requestedBy));
    }

    private void addPending(String user, PermissionDecision decision) {
        pendingRequests.put(user, decision);
    }

    private void removePendingRequest(String user) {
        pendingRequests.remove(user);
    }
//need to ask aviad about appending first or last.
    public void setFinalDecision(String user, Engine.ApprovalStatus approvalStatus) {
        PermissionDecision permissionRequest = pendingRequests.get(user);

        if (permissionRequest == null) {
            throw new RuntimeException("Request was not found. Could not finalize decision for user " + user);
        }

        PermissionDecision oldDecision = finalizedPermissions.get(user);

        if (oldDecision != null) {
            if (approvalStatus == Engine.ApprovalStatus.NO) {
                if (oldDecision.getApprovalStatus().equals(Engine.ApprovalStatus.NO)) {
                    permissionHistory.addFirst(oldDecision);
                }
                else {
                    permissionHistory.addFirst(permissionRequest);
                }
            } else if (approvalStatus == Engine.ApprovalStatus.YES) {
                permissionHistory.addFirst(oldDecision);
                addFinalizePermissions(user, permissionRequest);
            }
            permissionRequest.setApprovalStatus(approvalStatus);
            removePendingRequest(user);
        } else {
            permissionRequest.setApprovalStatus(approvalStatus);
            addFinalizePermissions(user, permissionRequest);
            removePendingRequest(user);
        }
    }


    public List<PermissionDecision> getPermissionHistory() {
        return this.permissionHistory;
    }
}
