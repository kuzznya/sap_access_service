package com.alpe.sap_access_service.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;

public class SAPTableRecord {
    // Map serializing as separate fields
    private LinkedHashMap<String, String> data = new LinkedHashMap<>();

    @JsonAnySetter
    public void addData(String key, String value) {
        data.put(key, value);
    }

    @JsonAnyGetter
    public LinkedHashMap<String, String> getData() {
        return data;
    }
}
