package com.alpe.sap_access_service.service;

import com.alpe.sap_access_service.model.User;
import com.alpe.sap_access_service.service.sap_modules.get_data.DatasetModule;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private DatasetModule datasetModule;

    public AuthService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
    }

    // Check user authentication in SAP
    public boolean auth(User user) {
        try {
            Object result = datasetModule.getDataSet(user.getSystem(),
                    user.getUsername(), user.getPassword(),
                    " ", " ", user.getLanguage(), " ", " ", " ", " ");
            return true;
        } catch (SOAPExceptionImpl ex) {
            return false;
        }
    }
}