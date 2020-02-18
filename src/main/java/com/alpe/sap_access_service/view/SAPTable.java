package com.alpe.sap_access_service.view;

import com.alpe.sap_access_service.model.sap_modules.get_data.SapMap;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class SAPTable {
    private LinkedList<String> systemNames;
    private LinkedList<String> textNames;
    private LinkedList<String> columnLen;
    private LinkedList<String> dataTypes;
    private LinkedList<String> domNames;
    private LinkedList<String> outputLen;
    private LinkedList<String> decimals;
    private int count;
    private LinkedList<LinkedList<String>> records;

    public SAPTable(LinkedHashMap<String, LinkedList<String>> map) {
        systemNames = map.get("fieldNames");
        map.remove("fieldNames");
        textNames = map.get("repText");
        map.remove("repText");
        columnLen = map.get("columnLen");
        map.remove("columnLen");
        dataTypes = map.get("dataTypes");
        map.remove("dataTypes");
        domNames = map.get("domNames");
        map.remove("domNames");
        outputLen = map.get("outputLen");
        map.remove("outputLen");
        decimals = map.get("decimals");
        map.remove("decimals");

        count = map.get(map.keySet().toArray()[0]).size();

        records = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            records.add(new LinkedList<>());
            for (String key : map.keySet()) {
                records.get(i).add(map.get(key).get(i));
            }
        }
    }

    public LinkedList<String> getSystemNames() {
        return systemNames;
    }

    public LinkedList<String> getTextNames() {
        return textNames;
    }

    public LinkedList<String> getColumnLen() {
        return columnLen;
    }

    public LinkedList<String> getDataTypes() {
        return dataTypes;
    }

    public LinkedList<String> getDomNames() {
        return domNames;
    }

    public LinkedList<String> getOutputLen() {
        return outputLen;
    }

    public LinkedList<String> getDecimals() {
        return decimals;
    }

    public int getCount() {
        return count;
    }

    public LinkedList<LinkedList<String>> getRecords() {
        return records;
    }
}
