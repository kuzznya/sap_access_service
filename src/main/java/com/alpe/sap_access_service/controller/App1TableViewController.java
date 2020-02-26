package com.alpe.sap_access_service.controller;

import com.alpe.sap_access_service.services.TableService;
import com.alpe.sap_access_service.services.sessions.Session;
import com.alpe.sap_access_service.services.sessions.SessionsService;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/apps/1")
public class App1TableViewController {

    private SessionsService sessionsService;
    private TableService tableService;

    public App1TableViewController(@Autowired SessionsService sessionsService,
                                   @Autowired TableService tableService) {
        this.sessionsService = sessionsService;
        this.tableService = tableService;
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
