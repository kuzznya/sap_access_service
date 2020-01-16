package com.alpe.sap_access_service.sessions;

import java.util.concurrent.TimeUnit;

public class Session {

    private final String system;
    private final String username;
    private final String password;
    private final int id;

    private long lastTimeAccessed;

    public Session(String system, String username, String password, int id) {
        this.system = system;
        this.username = username;
        this.password = password;
        this.id = id;
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public String getSystem() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return system;
    }

    public String getUsername() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return username;
    }

    public String getPassword() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return password;
    }

    public int getId() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return id;
    }

    public boolean auth() {
        //TODO authorization
        return true;
    }

    public long getLastTimeAccessed() {
        return lastTimeAccessed;
    }
}
