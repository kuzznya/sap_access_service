package com.alpe.sap_access_service.user.controller;

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
    Message authorize(@RequestBody User user) {
        try {
            String accessToken = usersService.createUser(user);
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
