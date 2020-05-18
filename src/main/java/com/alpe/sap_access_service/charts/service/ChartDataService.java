package com.alpe.sap_access_service.charts.service;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.charts.dao.ChartDataRepository;
import com.alpe.sap_access_service.charts.model.*;
import com.alpe.sap_access_service.sap.get_data.DatasetModule;
import com.alpe.sap_access_service.security.model.User;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    private ChartData<ChartValue<String>> getSimpleChartDataFromEntity(ChartDataEntity entity) throws com.fasterxml.jackson.core.JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        if (entity == null)
            throw new NullPointerException();

        CompletableFuture.runAsync(() -> updateEntity(entity));

        JavaType type = mapper.getTypeFactory().constructParametricType(SimpleChartData.class, String.class);
        ChartData<ChartValue<String>> data = mapper.readValue(entity.getChartData(), type);
        data.setId(entity.getId());
        return data;
    }

    public ChartData<? extends ChartValue<?>> getChartData(User user, long id) {
        var entity = repository.findById(id)
                .or(() -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND); })
                .get();

        if (entity.getCategoriesColumn() == null && entity.getCaptionsColumn() == null)
            return getChartData(user, entity.getTableName(), entity.getValuesColumn());
        else if (entity.getCategoriesColumn() != null && entity.getCaptionsColumn() == null)
            return getChartData(user, entity.getTableName(), entity.getValuesColumn(), entity.getCategoriesColumn());
        else
            return getChartData(user, entity.getTableName(), entity.getValuesColumn(), entity.getCategoriesColumn(),
                    entity.getCaptionsColumn());
    }

    public ChartData<ChartValue<String>> getChartData(User user, String tableName, String valuesColumn) {
        var mapper = new ObjectMapper();

        try {
            var entity = repository.findOneByTableNameAndValuesColumnAndCategoriesColumnAndCaptionsColumn(
                    tableName, valuesColumn, null, null);
            return getSimpleChartDataFromEntity(entity);

        } catch (Exception ex) {

            LinkedHashMap<String, LinkedList<String>> tableData;

            try {
                tableData = datasetModule.getDataSet(
                        user.getSystem(), user.getUsername(), user.getPassword(),
                        tableName, null, user.getLanguage(), null, null, null, valuesColumn);

            } catch (SOAPExceptionImpl sex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SAP connection error");
            }

            ChartData<ChartValue<String>> data = ChartDataFactory.createChartData(tableData.get(valuesColumn));

            saveEntity(tableName, valuesColumn, null, null, data);

            return data;
        }
    }

    public ChartData<ChartValue<String>> getChartData(User user, String tableName,
                                                      String valuesColumn, String captionsColumn) {
        var mapper = new ObjectMapper();

        try {
            var entity = repository.findOneByTableNameAndValuesColumnAndCategoriesColumnAndCaptionsColumn(
                    tableName, valuesColumn, null, captionsColumn);
            return getSimpleChartDataFromEntity(entity);

        } catch (Exception ex) {

            LinkedHashMap<String, LinkedList<String>> tableData;
            try {
                tableData = datasetModule.getDataSet(
                        user.getSystem(), user.getUsername(), user.getPassword(),
                        tableName, null, user.getLanguage(), null, null, null,
                        valuesColumn + " " + captionsColumn);
            } catch (Exception sex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SAP connection error");
            }

            ChartData<ChartValue<String>> data = ChartDataFactory.createChartData(tableData.get(valuesColumn), tableData.get(captionsColumn));

            saveEntity(tableName, valuesColumn, null, captionsColumn, data);

            return data;
        }

    }

    public ChartData<CategorizedChartValue<String, String>> getChartData(User user, String tableName,
                                                                         String valuesColumn, String categoriesColumn, String captionsColumn) {
        var mapper = new ObjectMapper();

        try {
            var entity = repository.findOneByTableNameAndValuesColumnAndCategoriesColumnAndCaptionsColumn(tableName, valuesColumn, categoriesColumn, captionsColumn);
            if (entity == null)
                throw new NullPointerException();

            CompletableFuture.runAsync(() -> updateEntity(entity));

            JavaType type = mapper.getTypeFactory().constructParametricType(CategorizedChartData.class,
                    String.class, String.class);
            ChartData<CategorizedChartValue<String, String>> data = mapper.readValue(entity.getChartData(), type);
            data.setId(entity.getId());
            return data;

        } catch (Exception ex) {

            LinkedHashMap<String, LinkedList<String>> tableData;
            try {
                tableData = datasetModule.getDataSet(
                        user.getSystem(), user.getUsername(), user.getPassword(),
                        tableName, null, user.getLanguage(), null, null, null,
                        valuesColumn + " " + categoriesColumn + " " + Objects.requireNonNullElse(captionsColumn, ""));
            } catch (SOAPExceptionImpl sex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SAP connection error");
            }

            ChartData<CategorizedChartValue<String, String>> data;

            if (captionsColumn == null)
                data = ChartDataFactory.createChartData(tableData.get(valuesColumn), tableData.get(categoriesColumn), null);
            else
                data = ChartDataFactory.createChartData(tableData.get(valuesColumn), tableData.get(categoriesColumn), tableData.get(captionsColumn));

            saveEntity(tableName, valuesColumn, categoriesColumn, captionsColumn, data);

            return data;
        }
    }

    public void saveEntity(String tableName, String valuesColumn, String categoriesColumn, String captionsColumn,
                           ChartData<? extends ChartValue<?>> data) {
        var mapper = new ObjectMapper();
        try {
            var entity = new ChartDataEntity(tableName, valuesColumn, categoriesColumn, captionsColumn,
                    mapper.writeValueAsString(data));
            repository.save(entity);
            data.setId(entity.getId());
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
