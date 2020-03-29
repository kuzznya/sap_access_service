package com.alpe.sap_access_service.service;

import com.alpe.sap_access_service.model.User;
import com.alpe.sap_access_service.service.sap_modules.get_data.DatasetModule;
import com.alpe.sap_access_service.model.SAPApplication;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class AvailableAppsService {

    @Value("${server.myaddress}")
    private String serverAddress;

    private DatasetModule datasetModule;

    public AvailableAppsService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
    }

    public LinkedList<SAPApplication> getAvailableApplications(User user) throws SOAPExceptionImpl {
        LinkedList<String> REPI2Data = datasetModule.getDataSet(user.getSystem(),
                user.getUsername(), user.getPassword(),
                " ", " ", user.getLanguage(), " ", " ", " ", " ").get("REPI2");
        LinkedList<SAPApplication> applications = new LinkedList<>();
        for (String el : REPI2Data) {
            // Transform app string from SAP to object, i.e. XXX. Name -> { id = XXX, name = Name }
            if (el.matches("[0-9]{3}[.]+.+")) {
                int id = Integer.parseInt(el.substring(0, 3));
                // TODO description
                applications.add(new SAPApplication(id, el.substring(4).trim(), null,
                        serverAddress + "/apps/" + id));
            }
        }
        return applications;
    }
}
