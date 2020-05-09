package com.alpe.sap_access_service.charts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleChartData<Tv> implements ChartData<ChartValue<Tv>> {
    List<ChartValue<Tv>> data;
}
