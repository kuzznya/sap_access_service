package com.alpe.sap_access_service.sessions;

import org.springframework.security.access.AccessDeniedException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionsController {

    private String hash(Session session) {
        return hash(session.getSystem(), session.getUsername(), session.getPassword(), session.getId());
    }

    private String hash(String system, String username, String password, int id) {
        String data = system + username + password + id;
        int hash = data.hashCode();
        StringBuilder result = new StringBuilder();
        while (hash > 0) {
            int currentVal = hash % 52;
            if (currentVal < 26)
                currentVal += 65;
            else
                currentVal += 97;
            char symbol = (char) currentVal;
            result.append(symbol);
        }
        return result.toString();
    }

    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String createSession(String system, String username, String password) {
        int id = 0;
        while (sessions.keySet().contains(hash(system, username, password, id)))
            id++;
        Session session = new Session(system, username, password, id);

        if (!session.auth())
            throw new AccessDeniedException("Error while trying to authorize");

        String accessToken = hash(session);
        sessions.put(accessToken, session);
        return accessToken;
    }

    public String getAccessToken(String system, String username, String password) {
        //TODO
    }

    public String getAccessToken(Session session) {
        //TODO
    }

    public void kill(String accessToken) {

    }

}
