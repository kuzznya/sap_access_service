package com.alpe.sap_access_service.charts.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
public class CategorizedChartData<Tv, Tc> implements ChartData<CategorizedChartValue<Tv, Tc>> {
    @NonNull
    List<CategorizedChartValue<Tv, Tc>> data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;
}
