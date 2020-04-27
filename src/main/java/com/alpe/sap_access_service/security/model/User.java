package com.alpe.sap_access_service.security.model;

import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Date;
import java.util.Objects;

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

    public User(@NotNull String system, @NotNull String username, @NotNull String password) {
        this.system = system;
        this.username = username;
        this.password = password;
        this.language = null;
        this.lastTimeAccessed = new Date();
        this.accessToken = new DigestUtils(SHA3_256).digestAsHex(system + username + password + id + new Date());
    }

    public User(@NotNull String system, @NotNull String username, @NotNull String password, Character language) {
        assert system != null;
        assert username != null;
        assert password != null;
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

    public void setSystem(@NotNull String system) {
        assert system != null;
        this.system = system;
        setAccessToken();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        assert username != null;
        this.username = username;
        setAccessToken();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        assert password != null;
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

    public void setLastTimeAccessed(@NotNull Date lastTimeAccessed) {
        this.lastTimeAccessed = lastTimeAccessed;
        setAccessToken();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return accessToken.equals(user.accessToken) &&
                system.equals(user.system) &&
                password.equals(user.password) &&
                language != null ? language.equals(user.language) : user.language == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, system, username, password, language, accessToken, lastTimeAccessed);
    }
}
