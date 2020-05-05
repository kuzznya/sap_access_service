package com.alpe.sap_access_service.charts.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class ChartDataEntity {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String tableName;
    @NotNull
    private String valuesColumn;
    private String categoriesColumn;
    private String captionsColumn;

    @Column
    @Lob
    private String chartData;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date creationDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date updateDate;

    public ChartDataEntity(String tableName, String valuesColumn, String categoriesColumn, String captionsColumn, String chartData) {
        this.tableName = tableName;
        this.valuesColumn = valuesColumn;
        this.categoriesColumn = categoriesColumn;
        this.captionsColumn = captionsColumn;
        this.chartData = chartData;
    }
}
