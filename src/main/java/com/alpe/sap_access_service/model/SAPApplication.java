package com.alpe.sap_access_service.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

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
    public void setDescription(String description) { this.description = description; }
    public String getUrl() { return this.url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SAPApplication that = (SAPApplication) o;
        return id == that.id &&
                name != null ? name.equals(that.name) : that.name == null &&
                description != null ? description.equals(that.description) : that.description == null &&
                url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, url);
    }
}
