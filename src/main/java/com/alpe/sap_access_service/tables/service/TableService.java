package com.alpe.sap_access_service.tables.service;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.tables.dao.TableRepository;
import com.alpe.sap_access_service.tables.model.SAPTableEntity;
import com.alpe.sap_access_service.user.model.User;
import com.alpe.sap_access_service.sap.get_data.DatasetModule;
import com.alpe.sap_access_service.tables.model.SAPTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.*;


@Service
public class TableService {

    DatasetModule datasetModule;

    private TableRepository repository;

    private int tableLifetime;

    private Timer tableCleaner;

    public TableService(@Autowired DatasetModule datasetModule,
                        @Autowired TableRepository repository) {
        this.datasetModule = datasetModule;
        this.repository = repository;
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

    // Get existing table by id
    public SAPTable getTable(User user, long id) {
        var entity = repository.findById(id)
                .or(() -> {throw new ResponseStatusException(HttpStatus.NOT_FOUND);})
                .get();

        return getTable(user, entity.getName(), entity.getLanguage(),
                entity.getWhere(), entity.getOrder(), entity.getGroup(), entity.getFieldNames());
    }

    // Get all table
    public SAPTable getTable(User user, String name, Character language,
                             String where, String order,
                             String group, String fieldNames) {
        var objectMapper = new ObjectMapper();
        SAPTableEntity entity = null;
        try {
            // Try to get table from local DB
            entity = repository.findByUserIdAndNameAndLanguageAndWhereAndOrderAndGroupAndFieldNames(user.getId(),
                    name, language, where, order, group, fieldNames);
            if (entity == null || !entity.isTableFull())
                throw new NullPointerException();

            return objectMapper.readValue(entity.getSapTableJSON(), SAPTable.class);
        } catch (Exception ex) {
            SAPTable table;

            try {
                // Get table from SAP
                var dataset = getDataset(user, name, null, language,
                        where, order, group, fieldNames);
                table = new SAPTable(dataset);
            } catch (SOAPExceptionImpl sex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SAP connection error");
            }

            if (entity != null) {
                // Update table
                table.setId(entity.getId());
                updateTable(entity, table, true);
            }
            else
                // Save table to local DB
                saveTable(user, name, true, language, where, order, group, fieldNames, table);

            return table;
        }
    }

    // Get existing table by id
    public SAPTable getTable(User user, long id, int offset, int count) {
        var entity = repository.findById(id)
                .or(() -> {throw new ResponseStatusException(HttpStatus.NOT_FOUND);})
                .get();

        return getTable(user, entity.getName(), offset, count, entity.getLanguage(),
                entity.getWhere(), entity.getOrder(), entity.getGroup(), entity.getFieldNames());
    }

    // Get table with subset of records (from - to)
    public SAPTable getTable(User user, String name, int offset, int count, Character language,
                             String where, String order,
                             String group, String fieldNames) {
        ObjectMapper objectMapper = new ObjectMapper();
        SAPTableEntity tableEntity = null;
        try {
            // Try to get table from local DB
            tableEntity = repository.findByUserIdAndNameAndLanguageAndWhereAndOrderAndGroupAndFieldNames(user.getId(),
                    name, language, where, order, group, fieldNames);
            // If no such table in DB or table in DB has less records than required
            if (tableEntity == null || tableEntity.getRecordsCount() < offset + count && !tableEntity.isTableFull())
                throw new NullPointerException();

            SAPTable table = objectMapper.readValue(tableEntity.getSapTableJSON(), SAPTable.class);
            table.setId(tableEntity.getId());

            // If offset is incorrect, return null
            if (tableEntity.isTableFull() && table.getRecordsCount() < offset)
                return new SAPTable(table.getId(), table.getColumns());

            // Load more records if table is not full (async)
            if (table.getRecordsCount() - (offset + count) < 100 && !tableEntity.isTableFull()) {
                val finalTableEntity1 = tableEntity;
                CompletableFuture.runAsync(() -> {
                    try {
                        loadMoreRecordsAndUpdateTable(user, name, offset + count, 100, language, where, order, group, fieldNames, finalTableEntity1);
                    } catch (Exception ignored) {}
                });
            }

            return table.getSubTable(offset, offset + count);
        } catch (Exception ex) {
            SAPTable table;
            try {
                // Get table from SAP
                var dataset = getDataset(user, name, offset + count + 1,
                        language, where, order, group, fieldNames);
                table = new SAPTable(dataset);
            } catch (SOAPExceptionImpl sex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (tableEntity == null) {
                // Save new table entity
                saveTable(user, name, table.getRecordsCount() < offset + count + 1,
                        language, where, order, group, fieldNames, table);

                //Load more records asynchronously
                CompletableFuture.runAsync(() -> {
                    // Load more records
                    try {
                        var entity = repository.getOne(table.getId());
                        loadMoreRecordsAndUpdateTable(user, name, offset + count, 100, language, where, order, group, fieldNames, entity);
                    } catch (Exception ignored) {}
                });
            }
            else {
                // Update table entity asynchronously
                table.setId(tableEntity.getId());

                val finalTableEntity = tableEntity;
                CompletableFuture.runAsync(() -> updateTable(finalTableEntity, table, table.getRecordsCount() < offset + count + 1))
                        .thenRun(() -> {
                            // Load more records
                            try {
                                loadMoreRecordsAndUpdateTable(user, name, offset + count, 100, language, where, order, group, fieldNames, finalTableEntity);
                            } catch (Exception ignored) {}
                        });
            }

            return table.getSubTable(offset, offset + count);
        }
    }

    // Load more records from SAP and update table in DB (method is required for calling asynchronously)
    private void loadMoreRecordsAndUpdateTable(User user, String name, int offset, int countOfNewRecords,
                                               Character language, String where, String order,
                                               String group, String fieldNames, SAPTableEntity entity) throws SOAPExceptionImpl {
        if (!entity.isTableFull()) {
            var newDataset = getDataset(user, name, offset + countOfNewRecords,
                    language, where, order, group, fieldNames);
            var newSapTable = new SAPTable(newDataset);
            updateTable(entity, newSapTable, newSapTable.getRecordsCount() < offset + countOfNewRecords);
        }
    }

    public void saveTable(User user, String name, Boolean full,
                          Character language, String where, String order,
                          String group, String fieldNames, SAPTable table) {
        var entity = new SAPTableEntity();
        entity.setUser(user);
        entity.setName(name);
        entity.setTableFull(full);
        entity.setLanguage(language);
        entity.setWhere(where);
        entity.setOrder(order);
        entity.setGroup(group);
        entity.setFieldNames(fieldNames);
        entity.setRecordsCount(table.getRecordsCount());

        var objectMapper = new ObjectMapper();
        try {
            entity.setSapTableJSON(objectMapper.writeValueAsString(table));
            repository.save(entity);
            table.setId(entity.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateTable(SAPTableEntity oldEntity, SAPTable updatedTable, Boolean full) {
        var objectMapper = new ObjectMapper();
        oldEntity.setUpdateDate(new Date());
        oldEntity.setRecordsCount(updatedTable.getRecordsCount());
        oldEntity.setTableFull(full);
        try {
            oldEntity.setSapTableJSON(objectMapper.writeValueAsString(updatedTable));
            repository.save(oldEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Get dataset (map of columns) from SAP
    public LinkedHashMap<String, LinkedList<String>> getDataset(User user,
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
            repository.deleteOldSAPTableEntities(tableLifetime);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

}
