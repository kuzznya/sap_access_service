package com.alpe.sap_access_service.service;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.model.AppUser;
import com.alpe.sap_access_service.model.SAPTableEntity;
import com.alpe.sap_access_service.service.sap_modules.get_data.DatasetModule;
import com.alpe.sap_access_service.model.SAPTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;


@Service
public class TableService {

    DatasetModule datasetModule;

    @Autowired
    private TableRepository tableRepository;

    private int tableLifetime;

    private Timer tableCleaner;

    public TableService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
        tableLifetime = SapAccessServiceApplication.getTokenLifetime();

        // Start deletion of old tables on timer event
        tableCleaner = new Timer();
        tableCleaner.schedule(new TimerTask() {
            @Override
            public void run() {
                deleteOldTables();
            }
        }, tableLifetime * 1000 / 2, tableLifetime * 1000 / 2);
    }

    // Get all table
    public SAPTable getTable(AppUser user, String name, Character language,
                             String where, String order,
                             String group, String fieldNames) throws SOAPExceptionImpl {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Try to get table from local DB
            SAPTableEntity tableEntity = tableRepository.findSAPTableEntityByAccessTokenAndParams(user.getAccessToken(), name, language, where, order, group, fieldNames);
            if (tableEntity == null || !tableEntity.isTableFull())
                throw new NullPointerException();

            return objectMapper.readValue(tableEntity.getSapTableJSON(), SAPTable.class);
        } catch (Exception ex) {
            // Get table from SAP
            LinkedHashMap<String, LinkedList<String>> dataset = getDataset(user, name, null, language,
                    where, order, group, fieldNames);
            SAPTable table = new SAPTable(dataset);

            // Run saving table to local DB asynchronously
            CompletableFuture.runAsync(() -> saveTable(user, name, true, language, where, order, group, fieldNames, table));
            return table;
        }
    }

    // Get table with subset of records (from - to)
    public SAPTable getTable(AppUser user, String name, int offset, int count, Character language,
                             String where, String order,
                             String group, String fieldNames) throws SOAPExceptionImpl {
        ObjectMapper objectMapper = new ObjectMapper();
        SAPTableEntity tableEntity = null;
        try {
            // Try to get table from local DB
            tableEntity = tableRepository.findSAPTableEntityByAccessTokenAndParams(user.getAccessToken(), name, language, where, order, group, fieldNames);
            // If no such table in DB or table in DB has less records than required
            if (tableEntity == null || tableEntity.getRecordsCount() < offset + count && !tableEntity.isTableFull())
                throw new NullPointerException();

            SAPTable table = objectMapper.readValue(tableEntity.getSapTableJSON(), SAPTable.class);

            // If offset is incorrect, return null
            if (tableEntity.isTableFull() && table.getRecordsCount() < offset)
                return null;

            // Load more records if table is not full (async)
            if (table.getRecordsCount() - (offset + count) < 100 && !tableEntity.isTableFull()) {
                final SAPTableEntity finalTableEntity1 = tableEntity;
                CompletableFuture.runAsync(() -> {
                    try {
                        loadMoreRecordsAndUpdateTable(user, name, offset + count, 100, language, where, order, group, fieldNames, finalTableEntity1);
                    } catch (Exception ignored) {}
                });
            }

            return table.getSubTable(offset, offset + count);
        } catch (Exception ex) {
            // Get table from SAP
            LinkedHashMap<String, LinkedList<String>> dataset = getDataset(user, name, offset + count + 1,
                    language, where, order, group, fieldNames);
            SAPTable table = new SAPTable(dataset);
            SAPTable subTable = table.getSubTable(offset, offset + count);

            if (tableEntity == null)
                // Save new table entity asynchronously
                CompletableFuture.runAsync(() -> saveTable(user, name, table.getRecordsCount() < offset + count + 1,
                        language, where, order, group, fieldNames, table)).thenRun(() -> {
                            // Load more records
                            try {
                                SAPTableEntity entity = tableRepository.findSAPTableEntityByAccessTokenAndParams(user.getAccessToken(), name, language, where, order, group, fieldNames);
                                loadMoreRecordsAndUpdateTable(user, name, offset + count, 100, language, where, order, group, fieldNames, entity);
                            } catch (Exception ignored) {}
                });
            else {
                // Update table entity asynchronously
                final SAPTableEntity finalTableEntity = tableEntity;
                CompletableFuture.runAsync(() -> updateTable(finalTableEntity, table, table.getRecordsCount() < offset + count + 1))
                        .thenRun(() -> {
                            // Load more records
                            try {
                                loadMoreRecordsAndUpdateTable(user, name, offset + count, 100, language, where, order, group, fieldNames, finalTableEntity);
                            } catch (Exception ignored) {}
                });
            }

            return subTable;
        }
    }

    // Load more records from SAP and update table in DB (method is required for calling asynchronously)
    private void loadMoreRecordsAndUpdateTable(AppUser user, String name, int offset, int countOfNewRecords,
                                               Character language, String where, String order,
                                               String group, String fieldNames, SAPTableEntity entity) throws SOAPExceptionImpl {
        if (!entity.isTableFull()) {
            LinkedHashMap<String, LinkedList<String>> newDataset = getDataset(user, name, offset + countOfNewRecords,
                    language, where, order, group, fieldNames);
            SAPTable newSapTable = new SAPTable(newDataset);
            updateTable(entity, newSapTable, newSapTable.getRecordsCount() < offset + countOfNewRecords);
        }
    }

    public void saveTable(AppUser user, String name, Boolean full,
                          Character language, String where, String order,
                          String group, String fieldNames, SAPTable table) {
        SAPTableEntity entity = new SAPTableEntity();
        entity.setAccessToken(user.getAccessToken());
        entity.setName(name);
        entity.setTableFull(full);
        entity.setLanguage(language);
        entity.setWhere(where);
        entity.setOrder(order);
        entity.setGroup(group);
        entity.setFieldNames(fieldNames);
        entity.setRecordsCount(table.getRecordsCount());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            entity.setSapTableJSON(objectMapper.writeValueAsString(table));
            tableRepository.save(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateTable(SAPTableEntity oldEntity, SAPTable updatedTable, Boolean full) {
        ObjectMapper objectMapper = new ObjectMapper();
        oldEntity.setUpdateDate(new Date());
        oldEntity.setRecordsCount(updatedTable.getRecordsCount());
        oldEntity.setTableFull(full);
        try {
            oldEntity.setSapTableJSON(objectMapper.writeValueAsString(updatedTable));
            tableRepository.save(oldEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Get dataset (map of columns) from SAP
    public LinkedHashMap<String, LinkedList<String>> getDataset(AppUser user,
                                                                String table, Integer recordsCount, Character language,
                                                                String where, String order,
                                                                String group, String fieldNames) throws SOAPExceptionImpl {
        String recordsCountStr = recordsCount != null ? String.valueOf(recordsCount) : null;
        language = (language != null && !language.equals(' ')) ? language : user.getLanguage();
        return datasetModule.getDataSet(user.getSystem(),
                user.getUsername(), user.getPassword(),
                table, recordsCountStr, language, where, order, group, fieldNames);
    }

    public void deleteOldTables() {
        try {
            tableRepository.deleteOldSAPTableEntities(tableLifetime);
            if (SapAccessServiceApplication.isSessionsInfo())
                System.out.println("Old tables deleted");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

}
