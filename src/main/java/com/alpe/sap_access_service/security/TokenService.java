package com.alpe.sap_access_service.security;

import com.alpe.sap_access_service.model.AppUser;
import com.alpe.sap_access_service.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final UsersService usersService;

    public TokenService(@Autowired UsersService usersService) {
        this.usersService = usersService;
    }

    public TokenAuthentication authenticate(final String token) {
        AppUser user = usersService.getUser(token);
        return new TokenAuthentication(token, true, user);
    }
}
