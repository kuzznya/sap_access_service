package com.alpe.sap_access_service.sessions;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SessionsController {

    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    public SessionsController() {
        (new Timer()).schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       killInactiveSessions();
                                   }
                               }, Math.round(1000 * 60 * SapAccessServiceApplication.getSessionLifetime() / 4),
                Math.round(1000 * 60 * SapAccessServiceApplication.getSessionLifetime() / 4));
    }

    public String createSession(String system, String username, String password) throws AccessDeniedException {
        int id = 0;
        while (sessions.containsKey(Session.hash(system, username, password, id)))
            id++;
        Session session = new Session(system, username, password, id);

        boolean authResult = session.auth();
        if (!authResult)
            throw new AccessDeniedException("Error while trying to authorize");

        sessions.put(session.getAccessToken(), session);
        return session.getAccessToken();
    }

    public String getAccessToken(String system, String username, int id) {
        for (String key : sessions.keySet()) {
            Session curSession = sessions.get(key);
            if (curSession.getSystem().equals(system) &&
                    curSession.getUsername().equals(username) &&
                    curSession.getId() == id)
                return curSession.getAccessToken();
        }
        return null;
    }

    public Session getSession(String accessToken) {
        return sessions.getOrDefault(accessToken, null);
    }

    public void killInactiveSessions() {
        for (String key : sessions.keySet()) {
            long sessionLifeTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) -
                    sessions.get(key).getLastTimeAccessed() * 60;
            if (sessionLifeTime >= SapAccessServiceApplication.getSessionLifetime())
                killSession(key);
        }
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
