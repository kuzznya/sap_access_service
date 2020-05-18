package com.alpe.sap_access_service.common.controller;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.util.Message;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;


@RestController
@CrossOrigin
public class APIController {

    @GetMapping("/systems")
    @ResponseBody
    LinkedList<String> getSystemsList() {
        return new LinkedList<>(SapAccessServiceApplication.getSystems());
    }

    @GetMapping("/token-lifetime")
    @ResponseBody
    Message getTokenLifetime() {
        return new Message("tokenLifetime", SapAccessServiceApplication.getTokenLifetime());
    }

}
