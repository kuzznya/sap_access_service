package com.alpe.sap_access_service.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;

public class AppUser {

    private final String system;
    private final String username;
    private final String password;
    private final int id;

    private String language = " ";

    private long lastTimeAccessed;

    public AppUser(String system, String username, String password, int id) {
        this.system = system;
        this.username = username;
        this.password = password;
        this.id = id;
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public AppUser(String system, String username, String password, int id, String language) {
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
        return hash(system, username, password, id);
    }

    public static String hash(String system, String username, String password, int id) {
        return new DigestUtils(SHA3_256).digestAsHex(system + username + password + id);
    }

    public void refresh() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public long getLastTimeAccessed() {
        return lastTimeAccessed;
    }
}
