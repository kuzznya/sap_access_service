package com.alpe.sap_access_service.security.controller;

import com.alpe.sap_access_service.security.model.AuthRequest;
import com.alpe.sap_access_service.security.model.TokenAuthentication;
import com.alpe.sap_access_service.security.service.UsersService;
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

    @PutMapping
    @ResponseBody
    String refreshToken(TokenAuthentication auth) {
        usersService.getUser(auth.getToken());
        return "Token refreshed";
    }

    @DeleteMapping
    ResponseEntity<?> deleteSession(TokenAuthentication auth) {
        String accessToken = auth.getToken();
        try {
            usersService.deleteUser(accessToken);
            return new ResponseEntity<>("Session deleted", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    ResponseEntity<?> checkToken(TokenAuthentication auth) {
        try {
            usersService.getUser(auth.getToken());
            return new ResponseEntity<>("Active session found", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>("No user with such access token", HttpStatus.BAD_REQUEST);
        }
    }
}
