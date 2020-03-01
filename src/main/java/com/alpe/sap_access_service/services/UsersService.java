package com.alpe.sap_access_service.services;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UsersService {

    private AuthService authService;

    private Map<String, AppUser> users = new ConcurrentHashMap<>();

    public UsersService(@Autowired AuthService authService) {
        this.authService = authService;

        (new Timer()).schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       deleteInactiveUsers();
                                   }
                               }, 1000 * SapAccessServiceApplication.getSessionLifetime() / 4,
                1000 * SapAccessServiceApplication.getSessionLifetime() / 4);
    }

    public String createUser(String system, String username, String password) throws AccessDeniedException {
        return createUser(system, username, password, null);
    }

    public String createUser(String system, String username, String password, String language) throws AccessDeniedException {
        int id = 0;
        while (users.containsKey(AppUser.hash(system, username, password, id)))
            id++;
        AppUser appUser;
        if (language != null)
            appUser = new AppUser(system, username, password, id, language);
        else
            appUser = new AppUser(system, username, password, id);

        boolean authResult = authService.auth(appUser);
        if (!authResult)
            throw new AccessDeniedException("Error while trying to authorize");

        users.put(appUser.getAccessToken(), appUser);
        return appUser.getAccessToken();
    }

    public String getAccessToken(String system, String username, int id) {
        for (String key : users.keySet()) {
            AppUser curUser = users.get(key);
            if (curUser.getSystem().equals(system) &&
                    curUser.getUsername().equals(username) &&
                    curUser.getId() == id)
                return key;
        }
        return null;
    }

    public AppUser getUser(String accessToken) {
        return users.getOrDefault(accessToken, null);
    }

    public void deleteInactiveUsers() {

        if (SapAccessServiceApplication.isSessionsInfo())
            System.out.println("=== Sessions life check ===");

        for (String key : users.keySet()) {

            long sessionLifeTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) -
                    users.get(key).getLastTimeAccessed();

            if (SapAccessServiceApplication.isSessionsInfo())
                System.out.println("Session (access token " + key + "): last time accessed " + sessionLifeTime + " seconds ago");

            if (sessionLifeTime >= SapAccessServiceApplication.getSessionLifetime()) {
                deleteUser(key);
                if (SapAccessServiceApplication.isSessionsInfo())
                    System.out.println("Session killed");
            }
        }

        if (SapAccessServiceApplication.isSessionsInfo())
            System.out.println("Count of active sessions: " + users.size() + "\n");
    }

    public void deleteUser(String accessToken) {
        users.remove(accessToken);
        try {
            users.remove(accessToken);
        } catch (Exception ex) {
            System.out.println("Cannot kill session with access token " + accessToken);
        }
    }

}
