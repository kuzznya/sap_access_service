package com.alpe.sap_access_service.controller;

import com.alpe.sap_access_service.model.AppUser;
import com.alpe.sap_access_service.security.TokenAuthentication;
import com.alpe.sap_access_service.services.TableService;
import com.alpe.sap_access_service.services.UsersService;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@RestController
@CrossOrigin
@RequestMapping("/apps/1")
public class App1TableViewController {

    @Value("${server.myaddress}")
    private String serverAddress;

    private UsersService usersService;
    private TableService tableService;

    public App1TableViewController(@Autowired UsersService usersService,
                                   @Autowired TableService tableService) {
        this.usersService = usersService;
        this.tableService = tableService;
    }

    @GetMapping
    ResponseEntity<?> getURLs(TokenAuthentication auth) {
        if (auth == null)
            return new ResponseEntity<>("Not authorized", HttpStatus.UNAUTHORIZED);

        LinkedList<String> URLs = new LinkedList<>();
        URLs.add(serverAddress + "/apps/1/table");
        URLs.add(serverAddress + "/apps/1/dataset");
        return new ResponseEntity<>(URLs, HttpStatus.OK);
    }

    @GetMapping("/table")
    ResponseEntity<?> getTable(@RequestParam(name = "name") String table,
                               @RequestParam(name = "recs_count", required = false) Integer recordsCount,
                               @RequestParam(name = "lang", required = false) String language,
                               @RequestParam(required = false) String where,
                               @RequestParam(required = false) String order,
                               @RequestParam(required = false) String group,
                               @RequestParam(name = "fields_names", required = false) String fieldsNames,
                               TokenAuthentication auth) {
        if (auth == null)
            return new ResponseEntity<>("Not authorized", HttpStatus.UNAUTHORIZED);
        AppUser user = (AppUser) auth.getPrincipal();

        String recordsCountStr = recordsCount != null ? String.valueOf(recordsCount) : null;

        try {
            return new ResponseEntity<>(tableService.getTable(user, table,
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
                                 TokenAuthentication auth) {
        if (auth == null)
            return new ResponseEntity<>("Not authorized", HttpStatus.UNAUTHORIZED);
        AppUser user = (AppUser) auth.getPrincipal();

        String recordsCountStr = recordsCount != null ? String.valueOf(recordsCount) : null;

        try {
            return new ResponseEntity<>(tableService.getDataset(user, table,
                    recordsCountStr, language, where, order, group, fieldsNames), HttpStatus.OK);
        } catch (SOAPExceptionImpl ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<Exception>(ex, HttpStatus.BAD_REQUEST);
        }
    }

}
