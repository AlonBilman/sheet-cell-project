package engine;

import manager.api.SheetManager;
import manager.impl.SheetManagerImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Engine {

    private final Set<String> activeUsers;
    private final Map<String,Set<SheetManager>> userMap;

    public Engine() {
        this.activeUsers = new HashSet<>();
        this.userMap = new HashMap<>();
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
        if (!userMap.containsKey(username)) {
            userMap.put(username, new HashSet<>());
        }
        userMap.get(username).add(sheetManager);
    }

}
