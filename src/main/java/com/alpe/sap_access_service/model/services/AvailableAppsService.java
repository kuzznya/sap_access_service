package com.alpe.sap_access_service.model.services;

import com.alpe.sap_access_service.model.sap_modules.get_data.DatasetModule;
import com.alpe.sap_access_service.model.sessions.Session;
import com.alpe.sap_access_service.view.SAPApplication;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class AvailableAppsService {

    DatasetModule datasetModule;

    public AvailableAppsService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
    }

    public LinkedList<SAPApplication> getAvailableApplications(Session session) throws SOAPExceptionImpl {
        LinkedList<String> REPI2Data = datasetModule.requestDataSet(session.getSystem(),
                session.getUsername(), session.getPassword(),
                " ", " ", session.getLanguage(), " ", " ", " ", " ").get("REPI2");
        LinkedList<SAPApplication> applications = new LinkedList<>();
        for (String el : REPI2Data) {
            if (el.matches("[0-9]{3}[.]+.+"))
                applications.add(new SAPApplication(el, null));
        }
        return applications;
    }
}