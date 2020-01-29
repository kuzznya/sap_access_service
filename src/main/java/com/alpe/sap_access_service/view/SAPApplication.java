package com.alpe.sap_access_service.view;

public class SAPApplication {
    private String name;
    private String description;

    public SAPApplication() {}

    public SAPApplication(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription() { this.description = description; }

}
