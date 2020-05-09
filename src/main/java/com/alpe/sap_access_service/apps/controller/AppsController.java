package com.alpe.sap_access_service.apps.controller;

import com.alpe.sap_access_service.apps.model.SAPApplication;
import com.alpe.sap_access_service.security.model.TokenAuthentication;
import com.alpe.sap_access_service.security.model.User;
import com.alpe.sap_access_service.apps.service.AvailableAppsService;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class AppsController {

    private AvailableAppsService appsService;

    public AppsController(@Autowired AvailableAppsService appsService) {
        this.appsService = appsService;
    }

    @GetMapping("/apps")
    @ResponseBody
    List<SAPApplication> getApplications(TokenAuthentication auth) {
        User user = (User) auth.getPrincipal();
        try {
            return appsService.getAvailableApplications(user);
        } catch (SOAPExceptionImpl ex) {
            throw new RuntimeException();
        }
    }
}
