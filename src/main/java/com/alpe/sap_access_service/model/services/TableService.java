package com.alpe.sap_access_service.model.services;

import com.alpe.sap_access_service.model.sessions.Session;
import com.alpe.sap_access_service.model.sap_modules.get_data.DatasetModule;
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

    public SAPTable getTable(Session session, String table, String fieldsQuan, String language,
                             String where, String order,
                             String group, String fieldNames) throws SOAPExceptionImpl {
        LinkedHashMap<String, LinkedList<String>> dataset = getDataset(session, table, fieldsQuan, language,
                where, order, group, fieldNames);
        return new SAPTable(dataset);
    }

    public LinkedHashMap<String, LinkedList<String>> getDataset(Session session,
                                                                String table, String fieldsQuan, String language,
                                                                String where, String order,
                                                                String group, String fieldNames) throws SOAPExceptionImpl {
        language = (language != null && !language.equals(" ")) ? language : session.getLanguage();
        return datasetModule.requestDataSet(session.getSystem(),
                session.getUsername(), session.getPassword(),
                table, fieldsQuan, language, where, order, group, fieldNames);
    }

}
