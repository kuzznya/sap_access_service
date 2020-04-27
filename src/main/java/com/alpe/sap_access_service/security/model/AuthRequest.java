package com.alpe.sap_access_service.security.model;

import java.util.Objects;

public class AuthRequest {
    private String system;
    private String username;
    private String password;
    private Character language = null;

    public AuthRequest() {}

    public AuthRequest(String system, String username, String password) {
        this.system = system;
        this.username = username;
        this.password = password;
    }

    public AuthRequest(String system, String username, String password, Character language) {
        this.system = system;
        this.username = username;
        this.password = password;
        this.language = language;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Character getLanguage() {
        return language;
    }

    public void setLanguage(Character language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthRequest request = (AuthRequest) o;
        return system != null ? system.equals(request.system) : request.system == null &&
                username != null ? username.equals(request.username) : request.username == null &&
                password != null ? password.equals(request.password) : request.password == null &&
                language != null ? language.equals(request.language) : request.language == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(system, username, password, language);
    }
}
