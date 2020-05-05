package com.alpe.sap_access_service.tables.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@Entity
@Data
public class SAPTableEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String accessToken;

    @Column(name = "tableName")
    private String name;
    @Column(name = "tableRecordsCount")
    private Integer recordsCount;
    @Column(name = "tableFull")
    @Getter(value = AccessLevel.PRIVATE)
    private Boolean tableFull;
    @Column(name = "tableLanguage")
    private Character language;
    @Column(name = "tableWhere")
    private String where;
    @Column(name = "tableOrder")
    private String order;
    @Column(name = "tableGroup")
    private String group;
    @Column(name = "tableFieldNames")
    private String fieldNames;

    @Column(name = "tableData")
    @Lob
    private String sapTableJSON;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    public boolean isTableFull() {
        return getTableFull();
    }
}
