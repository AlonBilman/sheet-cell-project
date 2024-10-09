package engine;

import dto.sheetDTO;
import manager.api.SheetManager;
import manager.impl.SheetManagerImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Engine {

    private final Set<String> activeUsers;
    private final Map<String, Set<SheetManagerImpl>> userMap;
    private final Set<String> sheetNames;

    public Engine() {
        this.activeUsers = new HashSet<>();
        this.userMap = new HashMap<>();
        sheetNames = new HashSet<>();
    }

    public synchronized void addUser(String user) {
        activeUsers.add(user);
    }

    public synchronized void removeUser(String user) {
        activeUsers.remove(user);
    }

    public synchronized boolean isUserExists(String username) {
        return activeUsers.contains(username);
    }

    public synchronized void addSheetManager(String username, SheetManagerImpl sheetManager) {
        if (sheetNames.contains(sheetManager.getSheetName()))
            throw new IllegalArgumentException("Sheet already exists (the name is taken)");
        if (!userMap.containsKey(username))
            userMap.put(username, new HashSet<>());
        userMap.get(username).add(sheetManager);
        sheetNames.add(sheetManager.getSheetName());
    }

    public synchronized sheetDTO getSheet(String sheetId, String userName) {
        Set<SheetManagerImpl> managers = userMap.get(userName);
        if (managers == null)
            throw new IllegalArgumentException("User " + userName + " does not exist");

        for (SheetManagerImpl manager : managers) {
            if (manager.getSheetName().equals(sheetId)) {
                return manager.Display();
            }
        }
        throw new IllegalArgumentException("Sheet " + sheetId + " does not exist for user " + userName);
    }
}
