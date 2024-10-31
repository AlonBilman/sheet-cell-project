package engine;

import dto.CellDataDto;
import dto.SheetDto;
import manager.impl.ChatManager;
import manager.impl.SheetManagerImpl;
import manager.impl.AppManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Engine {

    private final Set<String> activeUsers;
    private final Map<String, Set<AppManager>> userMap;
    private final Set<String> sheetNames;
    private final ChatManager chatManager;


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
        this.sheetNames = new HashSet<>();
        this.chatManager = new ChatManager();
    }

    public synchronized ChatManager getChatManager() {
        return chatManager;
    }

    public synchronized void addUser(String user) {
        activeUsers.add(user);
    }

    public synchronized void removeUser(String user) {
        if (!isUserExists(user))
            throw new IllegalArgumentException("There is no such user");
        activeUsers.remove(user);
        for (AppManager appManager : userMap.get(user)) {
            sheetNames.remove(appManager.getSheetManager().getSheetName());
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
        userMap.get(username).add(new AppManager(sheetManager));
        sheetNames.add(sheetManager.getSheetName());
    }

    public synchronized AppManager getManager(String username, String sheetId) {
        Set<AppManager> appManagers = userMap.get(username);
        if (appManagers == null || appManagers.isEmpty()) {
            throw new IllegalArgumentException("No appManagers found for user " + username);
        }

        return appManagers.stream()
                .filter(manager -> manager.getSheetManager().getSheetName().equals(sheetId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("AppManager for sheet " + sheetId + " does not exist for user " + username));
    }


    public synchronized SheetManagerImpl getSheetManager(String userName, String sheetId) {
        Set<AppManager> appManagers = userMap.get(userName);
        if (appManagers == null || appManagers.isEmpty()) {
            throw new IllegalArgumentException("Sheet " + sheetId + " does not exist for user " + userName);
        }
        return appManagers.stream()
                .map(AppManager::getSheetManager)
                .filter(sheetManager -> sheetManager.getSheetName().equals(sheetId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sheet " + sheetId + " does not exist for user " + userName));
    }

    public SheetManagerImpl getSheetManagerCopy(String username, String sheetId) {
        AppManager appManager = getManager(username, sheetId);
        return appManager.getManagerDeepCopyForDynamicChange();
    }


    public synchronized SheetDto getSheetDto(String sheetId, String userName) {
        AppManager appManager = getManager(userName, sheetId);
        SheetManagerImpl sheetManager = appManager.getSheetManager();
        return sheetManager.display();
    }

    public synchronized CellDataDto getCellDto(String userName, String sheetId, String cellId) {
        SheetManagerImpl manager = getSheetManager(userName, sheetId);
        return manager.showCell(cellId);
    }

    public synchronized Map<String, Set<AppManager>> getUserMap() {
        return userMap;
    }
}
