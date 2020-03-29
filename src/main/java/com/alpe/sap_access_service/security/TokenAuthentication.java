package com.alpe.sap_access_service.security;

import com.alpe.sap_access_service.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class TokenAuthentication implements Authentication {
    private String token;
    private boolean isAuthenticated;
    private User user;

    public TokenAuthentication(String token) {
        this.token = token;
        this.user = null;
    }

    public TokenAuthentication(String token, boolean isAuthenticated, User user) {
        this.token = token;
        this.isAuthenticated = isAuthenticated;
        this.user = user;
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
        return user;
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
        if (user != null)
            return user.getUsername();
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
