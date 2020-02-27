package com.alpe.sap_access_service.controller;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.model.BodyWithToken;
import com.alpe.sap_access_service.services.AvailableAppsService;
import com.alpe.sap_access_service.model.Session;
import com.alpe.sap_access_service.services.SessionsService;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.Set;


@RestController
@CrossOrigin
public class APIController {

    private SessionsService sessionsService;
    private AvailableAppsService appsService;

    public APIController(@Autowired SessionsService sessionsService,
                         @Autowired AvailableAppsService appsService) {
        this.sessionsService = sessionsService;
        this.appsService = appsService;
    }

    @GetMapping("/systems")
    LinkedList<String> getSystemsList() {
        Set<String> systemsSet = SapAccessServiceApplication.getSystems();
        return new LinkedList<>(systemsSet);
    }

    @GetMapping("/sessions-lifetime")
    ResponseEntity<?> getSessionsLifetime() {
        return new ResponseEntity<>(SapAccessServiceApplication.getSessionLifetime(), HttpStatus.OK);
    }

    @GetMapping("/apps")
    ResponseEntity<?> getApplications(@RequestBody BodyWithToken body) {
        String accessToken = body.getAccess_token();
        Session session = sessionsService.getSession(accessToken);
        try {
            if (session != null)
                return new ResponseEntity<>(appsService.getAvailableApplications(session), HttpStatus.OK);
            else
                return new ResponseEntity<>("No such session", HttpStatus.BAD_REQUEST);
        } catch (SOAPExceptionImpl ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<>(ex, HttpStatus.UNAUTHORIZED);
        }
    }

}
