package com.alpe.sap_access_service.charts.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "chartdata")
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

    public ChartDataEntity() {}

    public ChartDataEntity(String tableName, String valuesColumn, String categoriesColumn, String captionsColumn, String chartData) {
        this.tableName = tableName;
        this.valuesColumn = valuesColumn;
        this.categoriesColumn = categoriesColumn;
        this.captionsColumn = captionsColumn;
        this.chartData = chartData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getValuesColumn() {
        return valuesColumn;
    }

    public void setValuesColumn(String valuesColumn) {
        this.valuesColumn = valuesColumn;
    }

    public String getCategoriesColumn() {
        return categoriesColumn;
    }

    public void setCategoriesColumn(String categoriesColumn) {
        this.categoriesColumn = categoriesColumn;
    }

    public String getCaptionsColumn() {
        return captionsColumn;
    }

    public void setCaptionsColumn(String captionsColumn) {
        this.captionsColumn = captionsColumn;
    }

    public String getChartData() {
        return chartData;
    }

    public void setChartData(String chartData) {
        this.chartData = chartData;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChartDataEntity that = (ChartDataEntity) o;
        return id.equals(that.id) &&
                Objects.equals(tableName, that.tableName) &&
                Objects.equals(valuesColumn, that.valuesColumn) &&
                Objects.equals(categoriesColumn, that.categoriesColumn) &&
                Objects.equals(captionsColumn, that.captionsColumn) &&
                Objects.equals(chartData, that.chartData) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(updateDate, that.updateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tableName, valuesColumn, categoriesColumn, captionsColumn, chartData, creationDate, updateDate);
    }
}
