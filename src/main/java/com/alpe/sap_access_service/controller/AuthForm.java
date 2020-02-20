package com.alpe.sap_access_service.controller;

public class AuthForm {
    private String system;
    private String username;
    private String password;
    private String language = null;

    public AuthForm() {}

    public AuthForm(String system, String username, String password) {
        this.system = system;
        this.username = username;
        this.password = password;
    }

    public AuthForm(String system, String username, String password, String language) {
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
