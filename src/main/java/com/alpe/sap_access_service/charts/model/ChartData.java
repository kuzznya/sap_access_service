package com.alpe.sap_access_service.charts.model;

import java.util.LinkedList;

public interface ChartData<ValueType extends ChartValue<?>> {
    LinkedList<ValueType> getData();
    void setData(LinkedList<ValueType> data);
}
