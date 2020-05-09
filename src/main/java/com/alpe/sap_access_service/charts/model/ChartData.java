package com.alpe.sap_access_service.charts.model;

import java.util.List;

public interface ChartData<ValueType extends ChartValue<?>> {
    public List<ValueType> getData();
    public void setData(List<ValueType> data);

    public Long getId();
    public void setId(Long id);
}
