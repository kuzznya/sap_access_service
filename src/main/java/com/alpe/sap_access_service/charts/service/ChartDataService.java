package com.alpe.sap_access_service.charts.service;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.charts.dao.ChartDataRepository;
import com.alpe.sap_access_service.charts.model.CategorizedChartValue;
import com.alpe.sap_access_service.charts.model.ChartData;
import com.alpe.sap_access_service.charts.model.ChartDataEntity;
import com.alpe.sap_access_service.charts.model.ChartValue;
import com.alpe.sap_access_service.sap.get_data.DatasetModule;
import com.alpe.sap_access_service.security.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ChartDataService {

    DatasetModule datasetModule;

    ChartDataRepository repository;

    private Timer chartsCleaner;
    private int chartLifetime;

    public ChartDataService(@Autowired DatasetModule datasetModule,
                            @Autowired ChartDataRepository repository) {
        this.datasetModule = datasetModule;
        this.repository = repository;

        chartLifetime = SapAccessServiceApplication.getTokenLifetime();

        chartsCleaner = new Timer();
        chartsCleaner.schedule(new TimerTask() {
            @Override
            public void run() {
                deleteOldChartData();
            }
        }, chartLifetime * 1000 / 2, chartLifetime * 1000 / 2);
    }

    public ChartData<ChartValue<String>> getChartData(User user, String tableName, String valuesColumn) throws RuntimeException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ChartDataEntity entity = repository.findOneByTableNameAndValuesColumnAndCategoriesColumnAndCaptionsColumn(tableName, valuesColumn, null, null);
            if (entity == null)
                throw new NullPointerException();

            CompletableFuture.runAsync(() -> updateEntity(entity));
            return objectMapper.readValue(entity.getChartData(), objectMapper.getTypeFactory().constructParametricType(ChartData.class,
                    objectMapper.getTypeFactory().constructParametricType(ChartValue.class, String.class)));

        } catch (Exception ex) {

            LinkedHashMap<String, LinkedList<String>> tableData;

            try {
                tableData = datasetModule.getDataSet(
                        user.getSystem(), user.getUsername(), user.getPassword(),
                        tableName, null, user.getLanguage(), null, null, null, valuesColumn);

            } catch (SOAPExceptionImpl sex) {
                throw new RuntimeException();
            }

            ChartData<ChartValue<String>> data = ChartDataFactory.createChartData(tableData.get(valuesColumn));

            CompletableFuture.runAsync(() -> saveEntity(tableName, valuesColumn, null, null, data));

            return data;
        }
    }

    public ChartData<ChartValue<String>> getChartData(User user, String tableName,
                                     String valuesColumn, String captionsColumn) throws RuntimeException {
        ObjectMapper objectMapper = new ObjectMapper();

         try {
             ChartDataEntity entity = repository.findOneByTableNameAndValuesColumnAndCategoriesColumnAndCaptionsColumn(tableName, valuesColumn, null, captionsColumn);
             if (entity == null)
                 throw new NullPointerException();

             CompletableFuture.runAsync(() -> updateEntity(entity));
             return objectMapper.readValue(entity.getChartData(), objectMapper.getTypeFactory().constructParametricType(ChartData.class,
                     objectMapper.getTypeFactory().constructParametricType(ChartValue.class, String.class)));

         } catch (Exception ex) {


             LinkedHashMap<String, LinkedList<String>> tableData;
             try {
                 tableData = datasetModule.getDataSet(
                         user.getSystem(), user.getUsername(), user.getPassword(),
                         tableName, null, user.getLanguage(), null, null, null,
                         valuesColumn + " " + captionsColumn);
             } catch (Exception sex) {
                 throw new RuntimeException();
             }

             ChartData<ChartValue<String>> data = ChartDataFactory.createChartData(tableData.get(valuesColumn), tableData.get(captionsColumn));

             CompletableFuture.runAsync(() -> saveEntity(tableName, valuesColumn, null, captionsColumn, data));

             return data;
         }

    }

    public ChartData<CategorizedChartValue<String, String>> getChartData(User user, String tableName,
                                                         String valuesColumn, String categoriesColumn, String captionsColumn) throws RuntimeException {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ChartDataEntity entity = repository.findOneByTableNameAndValuesColumnAndCategoriesColumnAndCaptionsColumn(tableName, valuesColumn, categoriesColumn, captionsColumn);
            if (entity == null)
                throw new NullPointerException();

            CompletableFuture.runAsync(() -> updateEntity(entity));
            return objectMapper.readValue(entity.getChartData(), objectMapper.getTypeFactory().constructParametricType(ChartData.class,
                    objectMapper.getTypeFactory().constructParametricType(ChartValue.class, String.class)));

        } catch (Exception ex) {

            LinkedHashMap<String, LinkedList<String>> tableData;
            try {
                tableData = datasetModule.getDataSet(
                        user.getSystem(), user.getUsername(), user.getPassword(),
                        tableName, null, user.getLanguage(), null, null, null,
                        valuesColumn + " " + categoriesColumn + " " + Objects.requireNonNullElse(captionsColumn, ""));
            } catch (SOAPExceptionImpl sex) {
                throw new RuntimeException();
            }

            ChartData<CategorizedChartValue<String, String>> data;

            if (captionsColumn == null)
                data = ChartDataFactory.createChartData(tableData.get(valuesColumn), tableData.get(categoriesColumn), null);
            else
                data = ChartDataFactory.createChartData(tableData.get(valuesColumn), tableData.get(categoriesColumn), tableData.get(captionsColumn));

            CompletableFuture.runAsync(() -> saveEntity(tableName, valuesColumn, categoriesColumn, captionsColumn, data));

            return data;
        }
    }

    public void saveEntity(String tableName, String valuesColumn, String categoriesColumn, String captionsColumn,
                           ChartData<? extends ChartValue<?>> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ChartDataEntity entity = new ChartDataEntity(tableName, valuesColumn, categoriesColumn, captionsColumn,
                    objectMapper.writeValueAsString(data));
            repository.save(entity);
        } catch (Exception anotherEx) {
            System.err.println(anotherEx.getMessage());
        }
    }

    public void updateEntity(ChartDataEntity entity) {
        entity.setUpdateDate(new Date());
        repository.save(entity);
    }

    public void deleteOldChartData() {
        try {
            repository.deleteOldChartDataEntities(chartLifetime);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
