package com.alpe.sap_access_service.charts.model;

import java.util.Objects;

public class CategorizedChartValue<Tv, Tc> extends ChartValue<Tv> {

    private Tc category;

    public CategorizedChartValue(Tv value, Tc category) {
        super(value);
        this.category = category;
    }

    public CategorizedChartValue(Tv value, Tc category, String caption) {
        super(value, caption);
        this.category = category;
    }

    public Tc getCategory() {
        return this.category;
    }

    public void setCategory(Tc category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategorizedChartValue)) return false;
        if (!super.equals(o)) return false;
        CategorizedChartValue<?, ?> that = (CategorizedChartValue<?, ?>) o;
        return Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), category);
    }
}
