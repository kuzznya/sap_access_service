package com.alpe.sap_access_service.controller;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.services.AvailableAppsService;
import com.alpe.sap_access_service.services.TableService;
import com.alpe.sap_access_service.services.sessions.Session;
import com.alpe.sap_access_service.services.sessions.SessionsService;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;

import java.util.LinkedList;
import java.util.Set;


@RestController
@CrossOrigin
@RequestMapping("/api")
public class APIController {

    private final SessionsService sessionsService;

    private TableService tableService;
    private AvailableAppsService appsService;

    public APIController(@Autowired SessionsService sessionsService,
                         @Autowired TableService tableService,
                         @Autowired AvailableAppsService appsService) {
        this.sessionsService = sessionsService;
        this.tableService = tableService;
        this.appsService = appsService;
    }

    @GetMapping("/systems")
    LinkedList<String> getSystemsList() {
        Set<String> systemsSet = SapAccessServiceApplication.getSystems();
        return new LinkedList<>(systemsSet);
    }

    @PostMapping("/auth")
    ResponseEntity<?> authorize(@RequestBody AuthForm authForm) {
        String system = authForm.getSystem();
        String username = authForm.getUsername();
        String password = authForm.getPassword();
        String language = authForm.getLanguage();
        try {
            return new ResponseEntity<String>(sessionsService.createSession(system, username, password, language), HttpStatus.OK);
        } catch (AccessDeniedException ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<AccessDeniedException>(ex, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/auth")
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

    @DeleteMapping("/auth")
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

    @GetMapping("/auth")
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

    @GetMapping("/table")
    ResponseEntity<?> getTable(@RequestParam(name = "name") String table,
                               @RequestParam(name = "recs_count", required = false) Integer recordsCount,
                               @RequestParam(name = "lang", required = false) String language,
                               @RequestParam(required = false) String where,
                               @RequestParam(required = false) String order,
                               @RequestParam(required = false) String group,
                               @RequestParam(name = "fields_names", required = false) String fieldsNames,
                               @RequestBody BodyWithToken body) {
        String accessToken = body.getAccess_token();
        if (sessionsService.getSession(accessToken) == null) {
            return new ResponseEntity<>("Invalid access token", HttpStatus.UNAUTHORIZED);
        }

        String recordsCountStr = recordsCount != null ? String.valueOf(recordsCount) : null;

        Session session = sessionsService.getSession(accessToken);

        try {
            return new ResponseEntity<>(tableService.getTable(session, table,
                    recordsCountStr, language, where, order, group, fieldsNames), HttpStatus.OK);
        } catch (SOAPExceptionImpl ex) {
            return new ResponseEntity<>("SAP access error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/dataset")
    ResponseEntity<?> getDataset(@RequestParam(name = "name") String table,
                                 @RequestParam(name = "recs_count", required = false) Integer recordsCount,
                                 @RequestParam(name = "lang", required = false) String language,
                                 @RequestParam(required = false) String where,
                                 @RequestParam(required = false) String order,
                                 @RequestParam(required = false) String group,
                                 @RequestParam(name = "fields_names", required = false) String fieldsNames,
                                 @RequestBody BodyWithToken body) {
        String accessToken = body.getAccess_token();
        if (sessionsService.getSession(accessToken) == null) {
            return new ResponseEntity<>("Invalid access token", HttpStatus.UNAUTHORIZED);
        }

        String recordsCountStr = recordsCount != null ? String.valueOf(recordsCount) : null;

        Session session = sessionsService.getSession(accessToken);

        try {
            return new ResponseEntity<>(tableService.getDataset(session, table,
                    recordsCountStr, language, where, order, group, fieldsNames), HttpStatus.OK);
        } catch (SOAPExceptionImpl ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<Exception>(ex, HttpStatus.BAD_REQUEST);
        }
    }

}
