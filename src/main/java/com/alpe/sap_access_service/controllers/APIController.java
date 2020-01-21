package com.alpe.sap_access_service.controllers;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.sessions.SessionsController;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api")
public class APIController {

    private final SessionsController sessionsController = new SessionsController();

    public APIController() throws IOException { }

    @GetMapping("/systems")
    LinkedList<String> getSystemsList() {
        Set<String> systemsSet = SapAccessServiceApplication.getSystems();
        return new LinkedList<>(systemsSet);
    }

    @GetMapping("/login")
    ResponseEntity<?> login(@RequestParam(name = "system") String systemName,
                         @RequestParam String username,
                         @RequestParam String password) {
        try {
            return new ResponseEntity<String>(sessionsController.createSession(systemName, username, password), HttpStatus.OK);
        } catch (AccessDeniedException ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<AccessDeniedException>(ex, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/table")
    ResponseEntity<?> getTable(@RequestParam(name = "access_token") String accessToken,
                                                       @RequestParam(name = "name") String table,
                                                       @RequestParam(name = "fields_count", required = false) Integer fieldsCount,
                                                       @RequestParam(name = "lang", required = false) String language,
                                                       @RequestParam(required = false) String where,
                                                       @RequestParam(required = false) String order,
                                                       @RequestParam(required = false) String group,
                                                       @RequestParam(name = "fields_names", required = false) String fieldsNames) {

        if (sessionsController.getSession(accessToken) == null) {
            AccessDeniedException ex = new AccessDeniedException("Invalid access token");
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<AccessDeniedException>(ex, HttpStatus.UNAUTHORIZED);
        }

        String fieldsCountStr = fieldsCount != null ? String.valueOf(fieldsCount) : null;

        try {
            return new ResponseEntity<LinkedHashMap<String, LinkedList<String>>>(sessionsController.getSession(accessToken).requestDataSet(table,
                    fieldsCountStr, language, where, order, group, fieldsNames), HttpStatus.OK);
        } catch (SOAPExceptionImpl ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<Exception>(ex, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/session")
    ResponseEntity<?> deleteSession(@RequestParam(name = "access_token") String accessToken) {
        try {
            sessionsController.killSession(accessToken);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<Exception>(ex, HttpStatus.BAD_REQUEST);
        }
    }

}
