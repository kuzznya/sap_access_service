package com.alpe.sap_access_service.charts.service;

import com.alpe.sap_access_service.charts.model.*;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class ChartDataFactory {

    public static <Tv> ChartData<ChartValue<Tv>> createChartData(LinkedList<Tv> values) {
        LinkedList<ChartValue<Tv>> data = new LinkedList<>();

        for (Tv value : values)
            data.add(new ChartValue<>(value));

        return new SimpleChartData<>(data);
    }

    public static <Tv> ChartData<ChartValue<Tv>> createChartData(LinkedList<Tv> values, LinkedList<String> captions) {
        LinkedList<ChartValue<Tv>> data = new LinkedList<>();

        assert values.size() == captions.size();
        for (int i = 0; i < values.size(); i++)
            data.add(new ChartValue<>(values.get(i), captions.get(i)));

        return new SimpleChartData<>(data);
    }

    public static <Tv, Tc> ChartData<CategorizedChartValue<Tv, Tc>> createChartData(LinkedList<Tv> values,
                                                         LinkedList<Tc> categories,
                                                         LinkedList<String> captions) {
        LinkedList<CategorizedChartValue<Tv, Tc>> data = new LinkedList<>();

        assert values.size() == categories.size() && (captions == null || captions.size() == values.size());
        for (int i = 0; i < values.size(); i++) {
            if (captions == null)
                data.add(new CategorizedChartValue<>(values.get(i), categories.get(i)));
            else
                data.add(new CategorizedChartValue<>(values.get(i), categories.get(i), captions.get(i)));
        }

        return new CategorizedChartData<>(data);
    }

}
