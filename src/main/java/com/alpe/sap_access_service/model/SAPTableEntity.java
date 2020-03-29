package com.alpe.sap_access_service.model;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "saptables")
public class SAPTableEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "table_name")
    private String name;
    @Column(name = "table_records_count")
    private Integer recordsCount;
    @Column(name = "table_full")
    private Boolean tableFull;
    @Column(name = "table_language")
    private Character language;
    @Column(name = "table_where")
    private String where;
    @Column(name = "table_order")
    private String order;
    @Column(name = "table_group")
    private String group;
    @Column(name = "table_field_names")
    private String fieldNames;

    @Column(name = "table_data")
    @Lob
    private String sapTableJSON;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    private Date updateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSapTableJSON() {
        return sapTableJSON;
    }

    public void setSapTableJSON(String sapTableJSON) {
        this.sapTableJSON = sapTableJSON;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(Integer recordsCount) {
        this.recordsCount = recordsCount;
    }

    public Boolean isTableFull() {
        return tableFull;
    }

    public void setTableFull(Boolean tableFull) {
        this.tableFull = tableFull;
    }

    public Character getLanguage() {
        return language;
    }

    public void setLanguage(Character language) {
        this.language = language;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String fieldNames) {
        this.fieldNames = fieldNames;
    }
}