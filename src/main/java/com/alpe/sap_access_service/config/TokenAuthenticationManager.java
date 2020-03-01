package com.alpe.sap_access_service.config;

import com.alpe.sap_access_service.services.UsersService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import com.alpe.sap_access_service.model.User;
import org.springframework.stereotype.Service;

@Service
public class TokenAuthenticationManager implements AuthenticationManager {

    private final UsersService usersService;

    public TokenAuthenticationManager(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            if (authentication instanceof TokenAuthentication) {
                return processAuthentication((TokenAuthentication) authentication);
            } else {
                authentication.setAuthenticated(false);
                return authentication;
            }
        } catch (Exception ex) {
            if(ex instanceof AuthenticationServiceException)
                throw ex;
        }
        return authentication;
    }

    private TokenAuthentication processAuthentication(TokenAuthentication authentication) throws AuthenticationException {
        String token = authentication.getToken();

        return buildFullTokenAuthentication(authentication);
    }

    private TokenAuthentication buildFullTokenAuthentication(TokenAuthentication authentication) {
        User user = usersService.getUser(authentication.getToken());
        if (user == null)
            throw new AuthenticationServiceException("Cannot find user with this access token");
        return new TokenAuthentication(authentication.getToken(), true, user);
    }

}
