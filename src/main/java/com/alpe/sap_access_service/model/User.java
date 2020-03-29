package com.alpe.sap_access_service.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

import java.util.Date;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private long id;

    private String system;
    private String username;
    private String password;
    private Character language;

    @Column(name = "access_token", unique = true)
    private String accessToken;

    @Column(name = "accessed")
    private Date lastTimeAccessed;

    public User() {}

    public User(String system, String username, String password) {
        this.system = system;
        this.username = username;
        this.password = password;
        this.language = null;
        this.lastTimeAccessed = new Date();
        this.accessToken = new DigestUtils(SHA3_256).digestAsHex(system + username + password + id + new Date());
    }

    public User(String system, String username, String password, Character language) {
        this.system = system;
        this.username = username;
        this.password = password;
        this.language = language;
        this.lastTimeAccessed = new Date();
        this.accessToken = new DigestUtils(SHA3_256).digestAsHex(system + username + password + id + new Date());
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken() {
        if (accessToken == null && system != null && username != null && password != null && lastTimeAccessed != null)
            this.accessToken = new DigestUtils(SHA3_256).digestAsHex(system + username + password + id + new Date());
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
        setAccessToken();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        setAccessToken();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        setAccessToken();
    }

    public Character getLanguage() {
        return language;
    }

    public void setLanguage(Character language) {
        this.language = language;
        setAccessToken();
    }

    public Date getLastTimeAccessed() {
        return lastTimeAccessed;
    }

    public void setLastTimeAccessed(Date lastTimeAccessed) {
        this.lastTimeAccessed = lastTimeAccessed;
        setAccessToken();
    }
}
