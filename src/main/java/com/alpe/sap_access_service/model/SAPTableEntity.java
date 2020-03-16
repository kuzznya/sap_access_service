package com.alpe.sap_access_service.model;

import org.h2.util.json.JSONObject;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
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
    private String recordsCount;
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

    @Type(type = "JSON")
    @Column(name = "table_data")
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
}
