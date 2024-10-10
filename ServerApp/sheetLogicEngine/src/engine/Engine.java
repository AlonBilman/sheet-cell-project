package engine;

import dto.CellDataDTO;
import dto.sheetDTO;
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
        if (!isUserExists(user))
            throw new IllegalArgumentException("There is no such user");
        activeUsers.remove(user);
        userMap.remove(user);
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

    public synchronized SheetManagerImpl getSheetManager(String userName, String sheetId) {
        Set<SheetManagerImpl> managers = userMap.get(userName);
        if (managers == null)
            throw new IllegalArgumentException("User " + userName + " does not exist");

        for (SheetManagerImpl manager : managers) {
            if (manager.getSheetName().equals(sheetId)) {
                return manager;
            }
        }
        throw new IllegalArgumentException("Sheet " + sheetId + " does not exist for user " + userName);
    }

    public synchronized sheetDTO getSheetDTO(String sheetId, String userName) {
        SheetManagerImpl manager = getSheetManager(userName, sheetId);
        return manager.Display();
    }

    public synchronized CellDataDTO getCellDTO(String userName, String sheetId, String cellId) {
        SheetManagerImpl manager = getSheetManager(userName, sheetId);
        return manager.showCell(cellId);
    }
}
