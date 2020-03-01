package com.alpe.sap_access_service.services;

import com.alpe.sap_access_service.model.AppUser;
import com.alpe.sap_access_service.services.sap_modules.get_data.DatasetModule;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private DatasetModule datasetModule;

    public AuthService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
    }

    public boolean auth(AppUser appUser) {
        try {
            Object result = datasetModule.requestDataSet(appUser.getSystem(),
                    appUser.getUsername(), appUser.getPassword(),
                    " ", " ", appUser.getLanguage(), " ", " ", " ", " ");
            return true;
        } catch (SOAPExceptionImpl ex) {
            return false;
        }
    }
}
