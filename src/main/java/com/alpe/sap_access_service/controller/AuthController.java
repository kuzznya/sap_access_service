package com.alpe.sap_access_service.controller;

import com.alpe.sap_access_service.model.AuthRequest;
import com.alpe.sap_access_service.model.User;
import com.alpe.sap_access_service.security.TokenAuthentication;
import com.alpe.sap_access_service.service.UsersService;
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
            return new ResponseEntity<String>(usersService.createUser(system, username, password, language), HttpStatus.OK);
        } catch (AccessDeniedException ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<AccessDeniedException>(ex, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping
    ResponseEntity<?> refreshToken(TokenAuthentication auth) {
        if (auth == null)
            return new ResponseEntity<>("Not authorized", HttpStatus.UNAUTHORIZED);
        usersService.getUser(auth.getToken());
        return new ResponseEntity<>(null, HttpStatus.OK);
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
        if (auth != null) {
            try {
                usersService.getUser(auth.getToken());
                return new ResponseEntity<>("Active session found", HttpStatus.OK);
            } catch (Exception ex) {
                return new ResponseEntity<>("No user with such access token", HttpStatus.BAD_REQUEST);
            }
        }
        else
            return new ResponseEntity<>("Error: user not found", HttpStatus.UNAUTHORIZED);
    }
}
