package com.alpe.sap_access_service.controller;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.model.User;
import com.alpe.sap_access_service.security.TokenAuthentication;
import com.alpe.sap_access_service.service.AvailableAppsService;
import com.alpe.sap_access_service.service.UsersService;
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

    @GetMapping("/token-lifetime")
    ResponseEntity<?> getTokenLifetime() {
        return new ResponseEntity<>(SapAccessServiceApplication.getTokenLifetime(), HttpStatus.OK);
    }

    @GetMapping("/apps")
    ResponseEntity<?> getApplications(TokenAuthentication auth) {
        User user;
        try {
            user = (User) auth.getPrincipal();
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
