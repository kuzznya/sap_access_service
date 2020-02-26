package com.alpe.sap_access_service.view;

public class SAPApplication {
    private int id;
    private String name;
    private String description;

    public SAPApplication() {}

    public SAPApplication(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription() { this.description = description; }

}
