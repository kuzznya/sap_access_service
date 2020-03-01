package com.alpe.sap_access_service.controller;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.security.TokenAuthentication;
import com.alpe.sap_access_service.model.AppUser;
import com.alpe.sap_access_service.services.AvailableAppsService;
import com.alpe.sap_access_service.services.UsersService;
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

    private AvailableAppsService appsService;

    public APIController(@Autowired UsersService usersService,
                         @Autowired AvailableAppsService appsService) {
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
    ResponseEntity<?> getApplications(TokenAuthentication auth) {
        AppUser user;
        try {
            user = (AppUser) auth.getPrincipal();
        } catch (Exception ex) {
            return new ResponseEntity<>("Not authorized", HttpStatus.UNAUTHORIZED);
        }
        try {
            return new ResponseEntity<>(appsService.getAvailableApplications(user), HttpStatus.OK);
        } catch (SOAPExceptionImpl ex) {
            return new ResponseEntity<>("Not authorized", HttpStatus.UNAUTHORIZED);
        }
    }

}
