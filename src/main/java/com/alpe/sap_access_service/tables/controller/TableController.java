package com.alpe.sap_access_service.tables.controller;

import com.alpe.sap_access_service.security.model.User;
import com.alpe.sap_access_service.security.model.TokenAuthentication;
import com.alpe.sap_access_service.tables.model.SAPTable;
import com.alpe.sap_access_service.tables.service.TableService;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/apps/1")
public class TableController {

    private TableService tableService;

    public TableController(@Autowired TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    @ResponseBody
    List<String> getURLs() {
        var URLs = new LinkedList<String>();
        URLs.add("/apps/1/table");
        URLs.add("/apps/1/dataset");
        return URLs;
    }

    @GetMapping(value = "/table", params = {"id"})
    @ResponseBody
    SAPTable getTable(@RequestParam Long id,
                      @RequestParam(required = false) Integer offset,
                      @RequestParam(required = false) Integer count,
                      TokenAuthentication auth) {
        if (offset != null && offset < 0 || count != null && count < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Offset and count params cannot be less than zero");

        if (offset == null && count == null)
            return tableService.getTable(auth.getUser(), id);
        else
            return tableService.getTable(auth.getUser(), id,
                    Objects.requireNonNullElse(offset, 0), // If offset is not set, then get from the beginning
                    Objects.requireNonNullElse(count, 100000) // If count is null, then return 100000 records (or all from the offset)
            );
    }


    @GetMapping(value = "/table", params = {"name"})
    @ResponseBody
    SAPTable getTable(@RequestParam(name = "name") String table,
                               @RequestParam(required = false) Integer offset,
                               @RequestParam(required = false) Integer count,
                               @RequestParam(name = "lang", required = false) Character language,
                               @RequestParam(required = false) String where,
                               @RequestParam(required = false) String order,
                               @RequestParam(required = false) String group,
                               @RequestParam(name = "fields_names", required = false) String fieldsNames,
                               TokenAuthentication auth) {
        if (offset != null && offset < 0 || count != null && count < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "offset and count params cannot be less than zero");

        if (offset == null && count == null)
            return tableService.getTable(auth.getUser(), table,
                    language, where, order, group, fieldsNames);
        else
            return tableService.getTable(auth.getUser(), table,
                    Objects.requireNonNullElse(offset, 0), // If offset is not set, then get from the beginning
                    Objects.requireNonNullElse(count, 100000), // If count is null, then return 100000 records (or all from the offset)
                    language, where, order, group, fieldsNames);
    }

    @GetMapping("/dataset")
    @ResponseBody
    Map<String, LinkedList<String>> getDataset(@RequestParam(name = "name") String table,
                                               @RequestParam(name = "recs_count", required = false) Integer recordsCount,
                                               @RequestParam(name = "lang", required = false) Character language,
                                               @RequestParam(required = false) String where,
                                               @RequestParam(required = false) String order,
                                               @RequestParam(required = false) String group,
                                               @RequestParam(name = "fields_names", required = false) String fieldsNames,
                                               TokenAuthentication auth) {
        try {
            return tableService.getDataset(auth.getUser(), table,
                    recordsCount, language, where, order, group, fieldsNames);
        } catch (SOAPExceptionImpl ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SAP connection error");
        }
    }

}
