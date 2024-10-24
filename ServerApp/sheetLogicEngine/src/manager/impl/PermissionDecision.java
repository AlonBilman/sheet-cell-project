package manager.impl;

import engine.Engine;

public class PermissionDecision {
    private Engine.PermissionStatus permissionStatus;
    private Engine.ApprovalStatus approvalStatus;
    private final String name;

    public PermissionDecision(Engine.PermissionStatus permissionStatus, Engine.ApprovalStatus approvalStatus, String name) {
        this.permissionStatus = permissionStatus;
        this.approvalStatus = approvalStatus;
        this.name = name;
    }

    public String getName() {
        return name;
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
