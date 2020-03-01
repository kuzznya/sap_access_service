package com.alpe.sap_access_service.security;

import com.alpe.sap_access_service.model.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class TokenAuthentication implements Authentication {
    private String token;
    private boolean isAuthenticated;
    private AppUser appUser;

    public TokenAuthentication(String token) {
        this.token = token;
        this.appUser = null;
    }

    public TokenAuthentication(String token, boolean isAuthenticated, AppUser appUser) {
        this.token = token;
        this.isAuthenticated = isAuthenticated;
        this.appUser = appUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return appUser;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        this.isAuthenticated = b;
    }

    @Override
    public String getName() {
        if (appUser != null)
            return appUser.getUsername();
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

    public String getToken() {
        return token;
    }
}
