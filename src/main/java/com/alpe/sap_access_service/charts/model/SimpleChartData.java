package com.alpe.sap_access_service.charts.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
public class SimpleChartData<Tv> implements ChartData<ChartValue<Tv>> {
    @NonNull
    List<ChartValue<Tv>> data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;
}
