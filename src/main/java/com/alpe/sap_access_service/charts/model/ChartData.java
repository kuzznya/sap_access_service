package com.alpe.sap_access_service.charts.model;

import java.util.List;

public interface ChartData<ValueType extends ChartValue<?>> {
    List<ValueType> getData();
    void setData(List<ValueType> data);
}
