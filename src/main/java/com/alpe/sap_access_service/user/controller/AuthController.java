package com.alpe.sap_access_service.user.controller;

import com.alpe.sap_access_service.user.model.AuthRequest;
import com.alpe.sap_access_service.user.model.TokenAuthentication;
import com.alpe.sap_access_service.user.model.User;
import com.alpe.sap_access_service.user.service.UsersService;
import com.alpe.sap_access_service.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private UsersService usersService;

    public AuthController(@Autowired UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    @ResponseBody
    Message authorize(@RequestBody AuthRequest authRequest) {
        String system = authRequest.getSystem();
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();
        Character language = authRequest.getLanguage();
        try {
            String accessToken = usersService.createUser(system, username, password, language);
            return new Message("accessToken", accessToken);
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    @GetMapping
    @ResponseBody
    User getUser(TokenAuthentication auth) {
        return auth.getUser();
    }

    @DeleteMapping
    void deleteSession(TokenAuthentication auth) {
        usersService.deleteUser(auth.getUser());
    }
}
