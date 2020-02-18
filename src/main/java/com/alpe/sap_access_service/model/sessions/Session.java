package com.alpe.sap_access_service.model.sessions;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.model.sap_modules.get_data.SapMap;
import com.alpe.sap_access_service.view.SAPApplication;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Session {

    private final String system;
    private final String username;
    private final String password;
    private final int id;

    private String language = " ";

    private long lastTimeAccessed;

    public Session(String system, String username, String password, int id) {
        this.system = system;
        this.username = username;
        this.password = password;
        this.id = id;
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public Session(String system, String username, String password, int id, String language) {
        this.language = language;
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
        return password;
    }

    public int getId() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return id;
    }

    public String getLanguage() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return language;
    }

    public void setLanguage(String language) {
        if (language != null)
            this.language = language;
        else
            this.language = " ";
    }

    public String getAccessToken() {
        return hash(this.system, this.username, this.password, this.id);
    }

    public static String hash(String system, String username, String password, int id) {
        String data = system + username + password + id;
        int result = Math.abs(data.hashCode());
        return String.valueOf(result);
    }

    public void refresh() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public long getLastTimeAccessed() {
        return lastTimeAccessed;
    }
}
