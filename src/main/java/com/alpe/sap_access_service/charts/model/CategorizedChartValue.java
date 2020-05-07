package com.alpe.sap_access_service.charts.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategorizedChartValue<Tv, Tc> extends ChartValue<Tv> {

    private Tc category;

    public CategorizedChartValue(Tv value, Tc category) {
        super(value);
        this.category = category;
    }

    @JsonCreator
    public CategorizedChartValue(@JsonProperty("value") Tv value, @JsonProperty("category") Tc category, @JsonProperty("caption") String caption) {
        super(value, caption);
        this.category = category;
    }
}
