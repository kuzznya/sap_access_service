package com.alpe.sap_access_service.model.services;

import com.alpe.sap_access_service.model.sap_modules.get_data.DatasetModule;
import com.alpe.sap_access_service.model.sessions.Session;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private DatasetModule datasetModule;

    public AuthService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
    }

    public boolean auth(Session session) {
        try {
            Object result = datasetModule.requestDataSet(session.getSystem(),
                    session.getUsername(), session.getPassword(),
                    " ", " ", session.getLanguage(), " ", " ", " ", " ");
            return true;
        } catch (SOAPExceptionImpl ex) {
            return false;
        }
    }
}
