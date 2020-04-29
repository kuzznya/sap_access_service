package com.alpe.sap_access_service.charts.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ChartValue<Tv> {
    private Tv value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String caption;

    public ChartValue() {}

    public ChartValue(Tv value) {
        this.value = value;
    }

    @JsonCreator
    public ChartValue(@JsonProperty("value") Tv value, @JsonProperty("caption") String caption) {
        this.value = value;
        this.caption = caption;
    }

    public Tv getValue() {
        return value;
    }

    public void setValue(Tv value) {
        this.value = value;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChartValue<?> that = (ChartValue<?>) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(caption, that.caption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, caption);
    }
}
