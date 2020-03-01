package com.alpe.sap_access_service.services;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.model.User;
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

    private Map<String, User> sessions = new ConcurrentHashMap<>();

    public UsersService(@Autowired AuthService authService) {
        this.authService = authService;

        (new Timer()).schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       killInactiveSessions();
                                   }
                               }, 1000 * SapAccessServiceApplication.getSessionLifetime() / 4,
                1000 * SapAccessServiceApplication.getSessionLifetime() / 4);
    }

    public String createSession(String system, String username, String password) throws AccessDeniedException {
        return createSession(system, username, password, null);
    }

    public String createSession(String system, String username, String password, String language) throws AccessDeniedException {
        int id = 0;
        while (sessions.containsKey(User.hash(system, username, password, id)))
            id++;
        User user;
        if (language != null)
            user = new User(system, username, password, id, language);
        else
            user = new User(system, username, password, id);

        boolean authResult = authService.auth(user);
        if (!authResult)
            throw new AccessDeniedException("Error while trying to authorize");

        sessions.put(user.getAccessToken(), user);
        return user.getAccessToken();
    }

    public String getAccessToken(String system, String username, int id) {
        for (String key : sessions.keySet()) {
            User curUser = sessions.get(key);
            if (curUser.getSystem().equals(system) &&
                    curUser.getUsername().equals(username) &&
                    curUser.getId() == id)
                return curUser.getAccessToken();
        }
        return null;
    }

    public User getSession(String accessToken) {
        return sessions.getOrDefault(accessToken, null);
    }

    public void killInactiveSessions() {

        if (SapAccessServiceApplication.isSessionsInfo())
            System.out.println("=== Sessions life check ===");

        for (String key : sessions.keySet()) {

            long sessionLifeTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) -
                    sessions.get(key).getLastTimeAccessed();

            if (SapAccessServiceApplication.isSessionsInfo())
                System.out.println("Session (access token " + key + "): last time accessed " + sessionLifeTime + " seconds ago");

            if (sessionLifeTime >= SapAccessServiceApplication.getSessionLifetime()) {
                killSession(key);
                if (SapAccessServiceApplication.isSessionsInfo())
                    System.out.println("Session killed");
            }
        }

        if (SapAccessServiceApplication.isSessionsInfo())
            System.out.println("Count of active sessions: " + sessions.size() + "\n");
    }

    public void killSession(String accessToken) {
        sessions.remove(accessToken);
        try {
            sessions.remove(accessToken);
        } catch (Exception ex) {
            System.out.println("Cannot kill session with access token " + accessToken);
        }
    }

}
