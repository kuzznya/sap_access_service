package com.alpe.sap_access_service.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SAPTableRecord rec = (SAPTableRecord) o;
        if (data == null)
            return rec.data == null;
        if (!data.keySet().equals(rec.data.keySet()))
            return false;
        return data.equals(rec.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
