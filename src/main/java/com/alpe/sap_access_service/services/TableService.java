package com.alpe.sap_access_service.services;

import com.alpe.sap_access_service.model.User;
import com.alpe.sap_access_service.services.sap_modules.get_data.DatasetModule;
import com.alpe.sap_access_service.view.SAPTable;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.LinkedList;


@Service
public class TableService {

    DatasetModule datasetModule;

    public TableService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
    }

    public SAPTable getTable(User user, String table, String fieldsQuan, String language,
                             String where, String order,
                             String group, String fieldNames) throws SOAPExceptionImpl {
        LinkedHashMap<String, LinkedList<String>> dataset = getDataset(user, table, fieldsQuan, language,
                where, order, group, fieldNames);
        return new SAPTable(dataset);
    }

    public LinkedHashMap<String, LinkedList<String>> getDataset(User user,
                                                                String table, String fieldsQuan, String language,
                                                                String where, String order,
                                                                String group, String fieldNames) throws SOAPExceptionImpl {
        language = (language != null && !language.equals(" ")) ? language : user.getLanguage();
        return datasetModule.requestDataSet(user.getSystem(),
                user.getUsername(), user.getPassword(),
                table, fieldsQuan, language, where, order, group, fieldNames);
    }

}
