package common.dto.permission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//this is the data obj that will be sheared between the UI and the SERVER
public class PermissionData implements Serializable {

    List<Permission> permissions;
    List<Permission> history;

    public PermissionData(List<Permission> combinedList, List<Permission> history) {
        this.permissions = combinedList;
        this.history = history;
    }

    public PermissionData() {
        this.permissions = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public List<Permission> getHistory() {
        return history;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
