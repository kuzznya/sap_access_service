package com.alpe.sap_access_service.services;

import com.alpe.sap_access_service.model.AppUser;
import com.alpe.sap_access_service.services.sap_modules.get_data.DatasetModule;
import com.alpe.sap_access_service.view.SAPApplication;
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

    public LinkedList<SAPApplication> getAvailableApplications(AppUser user) throws SOAPExceptionImpl {
        LinkedList<String> REPI2Data = datasetModule.requestDataSet(user.getSystem(),
                user.getUsername(), user.getPassword(),
                " ", " ", user.getLanguage(), " ", " ", " ", " ").get("REPI2");
        LinkedList<SAPApplication> applications = new LinkedList<>();
        for (String el : REPI2Data) {
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
