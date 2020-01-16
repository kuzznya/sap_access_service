package com.alpe.sap_access_service.controllers;

import com.alpe.sap_access_service.properties.PropertiesHolder;
import com.alpe.sap_access_service.sessions.Session;
import com.alpe.sap_access_service.sessions.SessionsController;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedList;

@RestController
@RequestMapping("/api")
public class APIController {

    private final PropertiesHolder systemsProperties = new PropertiesHolder("systems.properties");

    private final SessionsController sessionsController = new SessionsController();

    public APIController() throws IOException { }

    @GetMapping("/systems")
    LinkedList<String> getSystemsList() {
        LinkedList<String> systems = new LinkedList<>();
        for (Object key : systemsProperties.getProperties().keySet())
            systems.add((String) key);
        return systems;
    }

    @GetMapping("/login/{systemName}/{username}/{password}")
    String login(@PathVariable String systemName,
                 @PathVariable String username,
                 @PathVariable String password) {
        try {
            return sessionsController.createSession(systemName, username, password);
        } catch (AccessDeniedException ex) {
            //TODO возврат ошибки
        }
    }

}
