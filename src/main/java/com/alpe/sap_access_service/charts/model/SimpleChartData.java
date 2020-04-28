package com.alpe.sap_access_service.charts.model;

import java.util.LinkedList;
import java.util.Objects;

public class SimpleChartData<Tv> implements ChartData<ChartValue<Tv>> {
    LinkedList<ChartValue<Tv>> data;

    public SimpleChartData() {}

    public SimpleChartData(LinkedList<ChartValue<Tv>> data) {
        this.data = data;
    }

    public LinkedList<ChartValue<Tv>> getData() {
        return data;
    }

    public void setData(LinkedList<ChartValue<Tv>> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleChartData<?> simpleChartData = (SimpleChartData<?>) o;
        return Objects.equals(data, simpleChartData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
