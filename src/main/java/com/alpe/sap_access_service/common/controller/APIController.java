package com.alpe.sap_access_service.common.controller;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.Set;


@RestController
@CrossOrigin
public class APIController {

    @GetMapping("/systems")
    LinkedList<String> getSystemsList() {
        Set<String> systemsSet = SapAccessServiceApplication.getSystems();
        return new LinkedList<>(systemsSet);
    }

    @GetMapping("/token-lifetime")
    ResponseEntity<?> getTokenLifetime() {
        return new ResponseEntity<>(SapAccessServiceApplication.getTokenLifetime(), HttpStatus.OK);
    }

}
