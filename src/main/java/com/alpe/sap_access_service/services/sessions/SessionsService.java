package com.alpe.sap_access_service.services.sessions;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class SessionsService {

    private AuthService authService;

    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    public SessionsService(@Autowired AuthService authService) {
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
        while (sessions.containsKey(Session.hash(system, username, password, id)))
            id++;
        Session session;
        if (language != null)
            session = new Session(system, username, password, id, language);
        else
            session = new Session(system, username, password, id);

        boolean authResult = authService.auth(session);
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
