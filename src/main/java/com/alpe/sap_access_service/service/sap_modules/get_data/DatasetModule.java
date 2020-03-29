package com.alpe.sap_access_service.service.sap_modules.get_data;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@Service
public class DatasetModule {

    // Get dataset (map of columns) from SAP
    public LinkedHashMap<String, LinkedList<String>> getDataSet(String system, String username, String password,
                                                                String table, String fieldsQuan, Character language,
                                                                String where, String order,
                                                                String group, String fieldNames)
            throws SOAPExceptionImpl {
        SapMap sm = new SapMap(table, fieldsQuan, language, where, order, group, fieldNames);
        String systemAddress = SapAccessServiceApplication.getSystemAddress(system);
        sm.dataFill(systemAddress, username, password);
        LinkedHashMap<String, LinkedList<String>> map = sm.getDataMap();
        map.put("columnLen", sm.getColumnLeng());
        map.put("fieldNames", sm.getFieldName());
        map.put("dataTypes", sm.getDataType());
        map.put("repText", sm.getRepText());
        map.put("domNames", sm.getDomName());
        map.put("outputLen", sm.getOutputLen());
        map.put("decimals", sm.getDecimals());
        return map;
    }

}
