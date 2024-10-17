package utils;

public class PermissionData {

    private final String userName;
    private final String permissionType;
    private final boolean approvedPermission;

    public PermissionData(String Name, String Permission, boolean approvedPermission) {
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
        if(approvedPermission) {
            return "Yes";
        }
        else return "No";
    }
}
