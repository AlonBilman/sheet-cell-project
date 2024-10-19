package manager.impl;

import engine.Engine;

public class PermissionDecision {
    private Engine.PermissionStatus permissionStatus;
    private Engine.ApprovalStatus approvalStatus;

    public PermissionDecision(Engine.PermissionStatus permissionStatus, Engine.ApprovalStatus approvalStatus) {
        this.permissionStatus = permissionStatus;
        this.approvalStatus = approvalStatus;
    }

    public Engine.PermissionStatus getPermissionStatus() {
        return permissionStatus;
    }

    public void setPermissionStatus(Engine.PermissionStatus permissionStatus) {
        this.permissionStatus = permissionStatus;
    }

    public Engine.ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Engine.ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
