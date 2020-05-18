package com.alpe.sap_access_service.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Message {
    Map<String, Object> data = new HashMap<>();

    public Message(String key, Object value) {
        data.put(key, value);
    }
}
