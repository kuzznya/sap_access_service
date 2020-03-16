package com.alpe.sap_access_service.model;

import com.fasterxml.jackson.annotation.JsonInclude;

public class SAPApplication {
    private int id;
    private String name;
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String url;

    public SAPApplication() {}

    public SAPApplication(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = null;
    }

    public SAPApplication(int id, String name, String description, String url) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription() { this.description = description; }
    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }

}
