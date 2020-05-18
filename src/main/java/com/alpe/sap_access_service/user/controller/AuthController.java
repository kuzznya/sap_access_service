package com.alpe.sap_access_service.user.controller;

import com.alpe.sap_access_service.user.model.AuthRequest;
import com.alpe.sap_access_service.user.model.TokenAuthentication;
import com.alpe.sap_access_service.user.model.User;
import com.alpe.sap_access_service.user.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private UsersService usersService;

    public AuthController(@Autowired UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    ResponseEntity<?> authorize(@RequestBody AuthRequest authRequest) {
        String system = authRequest.getSystem();
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();
        Character language = authRequest.getLanguage();
        try {
            return new ResponseEntity<>(usersService.createUser(system, username, password, language), HttpStatus.OK);
        } catch (AccessDeniedException ex) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
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
