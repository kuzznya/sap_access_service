package com.alpe.sap_access_service.charts.service;

import com.alpe.sap_access_service.charts.model.CategorizedChartValue;
import com.alpe.sap_access_service.charts.model.ChartData;
import com.alpe.sap_access_service.charts.model.ChartValue;
import com.alpe.sap_access_service.sap.get_data.DatasetModule;
import com.alpe.sap_access_service.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

@Service
public class ChartDataService {

    DatasetModule datasetModule;

    public ChartDataService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
    }

    public ChartData<ChartValue<String>> getChartData(User user, String tableName, String dataColumn) throws RuntimeException {
        try {
            LinkedHashMap<String, LinkedList<String>> tableData = datasetModule.getDataSet(
                    user.getSystem(), user.getUsername(), user.getPassword(),
                    tableName, null, user.getLanguage(), null, null, null, dataColumn);

            return ChartDataFactory.createChartData(tableData.get(dataColumn));

        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public ChartData<ChartValue<String>> getChartData(User user, String tableName,
                                     String dataColumn, String captionsColumn) throws RuntimeException {
        try {
            LinkedHashMap<String, LinkedList<String>> tableData = datasetModule.getDataSet(
                    user.getSystem(), user.getUsername(), user.getPassword(),
                    tableName, null, user.getLanguage(), null, null, null,
                    dataColumn + " " + captionsColumn);

            return ChartDataFactory.createChartData(tableData.get(dataColumn), tableData.get(captionsColumn));

        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public ChartData<CategorizedChartValue<String, String>> getChartData(User user, String tableName,
                                                         String dataColumn, String categoriesColumn, String captionsColumn) throws RuntimeException {
        try {
            LinkedHashMap<String, LinkedList<String>> tableData = datasetModule.getDataSet(
                    user.getSystem(), user.getUsername(), user.getPassword(),
                    tableName, null, user.getLanguage(), null, null, null,
                    dataColumn + " " + categoriesColumn + " " + Objects.requireNonNullElse(captionsColumn, ""));

            if (captionsColumn == null)
                return ChartDataFactory.createChartData(tableData.get(dataColumn), tableData.get(categoriesColumn), null);
            else
                return ChartDataFactory.createChartData(tableData.get(dataColumn), tableData.get(categoriesColumn), tableData.get(captionsColumn));

        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }
}
