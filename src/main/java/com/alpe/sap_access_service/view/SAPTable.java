package com.alpe.sap_access_service.view;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class SAPTable {
    private LinkedList<SAPTableColumn> columns;
    private int count;
    private LinkedList<SAPTableRecord> records;

    public SAPTable(LinkedHashMap<String, LinkedList<String>> map) {
        LinkedList<String> systemNames;
        LinkedList<String> textNames;
        LinkedList<String> columnLen;
        LinkedList<String> dataTypes;
        LinkedList<String> domNames;
        LinkedList<String> outputLen;
        LinkedList<String> decimals;

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

        columns = new LinkedList<>();
        for (int i = 0; i < systemNames.size(); i++) {
            columns.add(new SAPTableColumn(systemNames.get(i), textNames.get(i),
                    columnLen.get(i), dataTypes.get(i), domNames.get(i), outputLen.get(i), decimals.get(i)));
        }

        count = map.get(map.keySet().toArray()[0]).size();

        records = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            records.add(new SAPTableRecord());
            for (String key : map.keySet()) {
                records.get(i).addData(key, map.get(key).get(i));
            }
        }
    }

    public LinkedList<SAPTableColumn> getColumns() {
        return columns;
    }

    public int getCount() {
        return count;
    }

    public LinkedList<SAPTableRecord> getRecords() {
        return records;
    }
}