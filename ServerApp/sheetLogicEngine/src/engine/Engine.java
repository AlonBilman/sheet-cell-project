package engine;

import dto.CellDataDTO;
import dto.sheetDTO;
import manager.impl.SheetManagerImpl;
import manager.impl.Manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Engine {

    private final Set<String> activeUsers;
    private final Map<String, Set<Manager>> userMap;
    private final Set<String> sheetNames;


    public enum ApprovalStatus {
        PENDING {
            @Override
            public String toString() {
                return "Pending approval";
            }
        },
        YES {
            @Override
            public String toString() {
                return "Approved";
            }
        },
        NO {
            @Override
            public String toString() {
                return "Rejected";
            }
        }
    }

    public enum PermissionStatus {

        OWNER {
            @Override
            public String toString() {
                return "Owner";
            }
        },
        READER {
            @Override
            public String toString() {
                return "Reader";
            }
        },
        WRITER {
            @Override
            public String toString() {
                return "Writer";
            }
        },
    }

    public Engine() {
        this.activeUsers = new HashSet<>();
        this.userMap = new HashMap<>();
        sheetNames = new HashSet<>();
    }

    public synchronized void addUser(String user) {
        activeUsers.add(user);
    }

    public synchronized void removeUser(String user) {
        if (!isUserExists(user))
            throw new IllegalArgumentException("There is no such user");
        activeUsers.remove(user);
        for (Manager manager : userMap.get(user)) {
            sheetNames.remove(manager.getSheetManager().getSheetName());
        }
        userMap.remove(user);
    }

    public synchronized boolean isUserExists(String username) {
        return activeUsers.stream()
                .anyMatch(user -> user.equalsIgnoreCase(username));
    }

    public synchronized void addSheetManager(String username, SheetManagerImpl sheetManager, boolean init) {
        if (sheetNames.contains(sheetManager.getSheetName()) && init)
            throw new IllegalArgumentException("Sheet already exists (the name is taken)");
        if (!userMap.containsKey(username))
            userMap.put(username, new HashSet<>());
        userMap.get(username).add(new Manager(sheetManager));
        sheetNames.add(sheetManager.getSheetName());
    }

    public synchronized Manager getManager(String username, String sheetId) {
        Set<Manager> managers = userMap.get(username);
        if (managers == null || managers.isEmpty()) {
            throw new IllegalArgumentException("No managers found for user " + username);
        }

        return managers.stream()
                .filter(manager -> manager.getSheetManager().getSheetName().equals(sheetId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Manager for sheet " + sheetId + " does not exist for user " + username));
    }


    public synchronized SheetManagerImpl getSheetManager(String userName, String sheetId) {
        Set<Manager> managers = userMap.get(userName);
        if (managers == null || managers.isEmpty()) {
            throw new IllegalArgumentException("Sheet " + sheetId + " does not exist for user " + userName);
        }
        return managers.stream()
                .map(Manager::getSheetManager)
                .filter(sheetManager -> sheetManager.getSheetName().equals(sheetId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sheet " + sheetId + " does not exist for user " + userName));
    }

    public SheetManagerImpl getSheetManagerCopy(String username, String sheetId) {
        Manager manager = getManager(username, sheetId);
        return manager.getManagerDeepCopyForDynamicChange();
    }


    public synchronized sheetDTO getSheetDTO(String sheetId, String userName) {
        Manager manager = getManager(userName, sheetId);
        SheetManagerImpl sheetManager = manager.getSheetManager();
        return sheetManager.Display();
    }

    public synchronized CellDataDTO getCellDTO(String userName, String sheetId, String cellId) {
        SheetManagerImpl manager = getSheetManager(userName, sheetId);
        return manager.showCell(cellId);
    }

    public synchronized Map<String, Set<Manager>> getUserMap() {
        return userMap;
    }
}
