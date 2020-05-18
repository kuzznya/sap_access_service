package com.alpe.sap_access_service.security;

import com.alpe.sap_access_service.user.model.TokenAuthentication;
import com.alpe.sap_access_service.user.model.User;
import com.alpe.sap_access_service.user.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialException;
import java.util.NoSuchElementException;

@Service
public class TokenService {
    private final UsersService usersService;

    public TokenService(@Autowired UsersService usersService) {
        this.usersService = usersService;
    }

    public TokenAuthentication authenticate(final String token) throws CredentialException {
        try {
            User user = usersService.getUser(token);
            return new TokenAuthentication(token, true, user);
        } catch (NoSuchElementException ex) {
            throw new CredentialException("User not found");
        }
    }
}
