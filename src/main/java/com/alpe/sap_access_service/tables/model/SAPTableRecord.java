package com.alpe.sap_access_service.tables.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.LinkedHashMap;

@EqualsAndHashCode
public class SAPTableRecord {
    // Map serializing as separate fields
    private HashMap<String, String> data = new LinkedHashMap<>();

    @JsonAnySetter
    public void addData(String key, String value) {
        data.put(key, value);
    }

    @JsonAnyGetter
    public HashMap<String, String> getData() {
        return data;
    }
}
