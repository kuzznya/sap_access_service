package com.alpe.sap_access_service.apps.service;

import com.alpe.sap_access_service.user.model.User;
import com.alpe.sap_access_service.sap.get_data.DatasetModule;
import com.alpe.sap_access_service.apps.model.SAPApplication;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;

@Service
public class AvailableAppsService {

    private DatasetModule datasetModule;

    public AvailableAppsService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
    }

    public LinkedList<SAPApplication> getAvailableApplications(User user) {
        LinkedList<String> REPI2Data;
        try {
            REPI2Data = datasetModule.getDataSet(user.getSystem(),
                    user.getUsername(), user.getPassword(),
                    " ", " ", user.getLanguage(), " ", " ", " ", " ").get("REPI2");
        } catch (SOAPExceptionImpl ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LinkedList<SAPApplication> applications = new LinkedList<>();
        for (String el : REPI2Data) {
            // Transform app string from SAP to object, i.e. XXX. Name -> { id = XXX, name = Name }
            if (el.matches("[0-9]{3}[.]+.+")) {
                int id = Integer.parseInt(el.substring(0, 3));
                // TODO description
                applications.add(new SAPApplication(id, el.substring(4).trim(), null, "/apps/" + id));
            }
        }
        return applications;
    }
}
