package com.alpe.sap_access_service.apps.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SAPApplication {
    private int id;
    private String name;
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String url;

    public SAPApplication(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = null;
    }
}
