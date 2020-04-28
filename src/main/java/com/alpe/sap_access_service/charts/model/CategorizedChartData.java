package com.alpe.sap_access_service.charts.model;

import java.util.LinkedList;
import java.util.Objects;

public class CategorizedChartData<Tv, Tc> implements ChartData<CategorizedChartValue<Tv, Tc>> {
    LinkedList<CategorizedChartValue<Tv, Tc>> data;

    public CategorizedChartData() {}

    public CategorizedChartData(LinkedList<CategorizedChartValue<Tv, Tc>> data) {
        this.data = data;
    }

    @Override
    public LinkedList<CategorizedChartValue<Tv, Tc>> getData() {
        return data;
    }

    @Override
    public void setData(LinkedList<CategorizedChartValue<Tv, Tc>> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategorizedChartData<?, ?> that = (CategorizedChartData<?, ?>) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
