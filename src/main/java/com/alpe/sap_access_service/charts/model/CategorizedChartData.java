package com.alpe.sap_access_service.charts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorizedChartData<Tv, Tc> implements ChartData<CategorizedChartValue<Tv, Tc>> {
    LinkedList<CategorizedChartValue<Tv, Tc>> data;
}
