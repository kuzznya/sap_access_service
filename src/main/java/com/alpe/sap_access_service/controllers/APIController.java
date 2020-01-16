package com.alpe.sap_access_service.controllers;

import com.alpe.sap_access_service.properties.PropertiesHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;

@RestController
@RequestMapping("/api")
public class APIController {

    private final PropertiesHolder systemsProperties = new PropertiesHolder("systems.properties");

    public APIController() throws IOException { }

    @GetMapping("/systems")
    LinkedList<String> getSystemsList() {
        LinkedList<String> systems = new LinkedList<>();
        for (Object key : systemsProperties.getProperties().keySet())
            systems.add((String) key);
        return systems;
    }
}
