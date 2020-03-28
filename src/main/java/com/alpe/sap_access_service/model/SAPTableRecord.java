package com.alpe.sap_access_service.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.LinkedHashMap;

public class SAPTableRecord {
    private LinkedHashMap<String, String> data = new LinkedHashMap<>();

    public void addData(String key, String value) {
        data.put(key, value);
    }

    @JsonAnyGetter
    public LinkedHashMap<String, String> getData() {
        return data;
    }
}
