package com.alpe.sap_access_service.controller;

import com.alpe.sap_access_service.services.sessions.Session;
import com.alpe.sap_access_service.services.sessions.SessionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    private SessionsService sessionsService;

    public AuthController(@Autowired SessionsService sessionsService) {
        this.sessionsService = sessionsService;
    }

    @PostMapping
    ResponseEntity<?> authorize(@RequestBody AuthRequest authRequest) {
        String system = authRequest.getSystem();
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();
        String language = authRequest.getLanguage();
        try {
            return new ResponseEntity<String>(sessionsService.createSession(system, username, password, language), HttpStatus.OK);
        } catch (AccessDeniedException ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<AccessDeniedException>(ex, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping
    ResponseEntity<?> refreshToken(@RequestBody BodyWithToken body) {
        String accessToken = body.getAccess_token();
        try {
            sessionsService.getSession(accessToken).refresh();
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    ResponseEntity<?> deleteSession(@RequestBody BodyWithToken body) {
        String accessToken = body.getAccess_token();
        try {
            sessionsService.killSession(accessToken);
            return new ResponseEntity<>("Session deleted", HttpStatus.OK);
        } catch (Exception ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<Exception>(ex, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    ResponseEntity<?> checkToken(@RequestBody BodyWithToken body) {
        String accessToken = body.getAccess_token();
        Session session = sessionsService.getSession(accessToken);
        if (session != null) {
            session.refresh();
            return new ResponseEntity<>("Active session found", HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("Error: session with access token " + accessToken + " not found", HttpStatus.UNAUTHORIZED);
    }
}
