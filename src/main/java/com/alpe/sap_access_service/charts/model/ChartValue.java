package com.alpe.sap_access_service.charts.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class ChartValue<Tv> {
    private Tv value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String caption;

    public ChartValue(Tv value) {
        this.value = value;
    }

    @JsonCreator
    public ChartValue(@JsonProperty("value") Tv value, @JsonProperty("caption") String caption) {
        this.value = value;
        this.caption = caption;
    }
}
