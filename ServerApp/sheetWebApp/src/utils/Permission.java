package utils;

import java.io.Serializable;

public class Permission implements Serializable {

    private final String userName;
    private final String permissionType;
    private final String approvedPermission;

    public Permission(String Name, String Permission, String approvedPermission) {
        this.userName = Name;
        this.permissionType = Permission;
        this.approvedPermission = approvedPermission;
    }

    public String getUserName() {
        return userName;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public String getApprovedPermission() {
        return approvedPermission;
    }
}