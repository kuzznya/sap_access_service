package com.alpe.sap_access_service.tables.controller;

import com.alpe.sap_access_service.security.model.User;
import com.alpe.sap_access_service.security.model.TokenAuthentication;
import com.alpe.sap_access_service.tables.service.TableService;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@RestController
@CrossOrigin
@RequestMapping("/apps/1")
public class TableController {

    private TableService tableService;

    public TableController(@Autowired TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    ResponseEntity<?> getURLs() {
        LinkedList<String> URLs = new LinkedList<>();
        URLs.add("/apps/1/table");
        URLs.add("/apps/1/dataset");
        return new ResponseEntity<>(URLs, HttpStatus.OK);
    }

    @GetMapping("/table")
    ResponseEntity<?> getTable(@RequestParam(name = "name") String table,
                               @RequestParam(required = false) Integer offset,
                               @RequestParam(required = false) Integer count,
                               @RequestParam(name = "lang", required = false) Character language,
                               @RequestParam(required = false) String where,
                               @RequestParam(required = false) String order,
                               @RequestParam(required = false) String group,
                               @RequestParam(name = "fields_names", required = false) String fieldsNames,
                               TokenAuthentication auth) {
        User user = (User) auth.getPrincipal();

        if (offset != null && offset < 0 || count != null && count < 0)
            return new ResponseEntity<>("offset and count params cannot be less than zero", HttpStatus.BAD_REQUEST);

        try {
            if (offset == null && count == null)
                return new ResponseEntity<>(tableService.getTable(user, table,
                        language, where, order, group, fieldsNames), HttpStatus.OK);
            else if (offset == null)
                return new ResponseEntity<>(tableService.getTable(user, table, 0, count,
                        language, where, order, group, fieldsNames), HttpStatus.OK);
            else if (count == null)
                // If offset is set & count is null then all records of the table from offset should be returned
                // Now the count is manually set as 10000
                return new ResponseEntity<>(tableService.getTable(user, table, offset, 100000,
                        language, where, order, group, fieldsNames), HttpStatus.OK);
            else
                return new ResponseEntity<>(tableService.getTable(user, table, offset, count,
                        language, where, order, group, fieldsNames), HttpStatus.OK);
        } catch (SOAPExceptionImpl ex) {
            return new ResponseEntity<>("SAP access error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/dataset")
    ResponseEntity<?> getDataset(@RequestParam(name = "name") String table,
                                 @RequestParam(name = "recs_count", required = false) Integer recordsCount,
                                 @RequestParam(name = "lang", required = false) Character language,
                                 @RequestParam(required = false) String where,
                                 @RequestParam(required = false) String order,
                                 @RequestParam(required = false) String group,
                                 @RequestParam(name = "fields_names", required = false) String fieldsNames,
                                 TokenAuthentication auth) {
        User user = (User) auth.getPrincipal();

        try {
            return new ResponseEntity<>(tableService.getDataset(user, table,
                    recordsCount, language, where, order, group, fieldsNames), HttpStatus.OK);
        } catch (SOAPExceptionImpl ex) {
            ex.setStackTrace(new StackTraceElement[0]);
            return new ResponseEntity<Exception>(ex, HttpStatus.BAD_REQUEST);
        }
    }

}