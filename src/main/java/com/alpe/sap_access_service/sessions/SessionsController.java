package com.alpe.sap_access_service.sessions;

import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionsController {

    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String createSession(String system, String username, String password) throws IOException {
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

    public void kill(String accessToken) {

    }

}
