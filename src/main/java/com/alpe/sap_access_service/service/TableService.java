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

        tableCleaner = new Timer();
        tableCleaner.schedule(new TimerTask() {
            @Override
            public void run() {
                deleteOldTables();
            }
        }, tableLifetime * 1000 / 2, tableLifetime * 1000 / 2);
    }

    public SAPTable getTable(AppUser user, String name, Character language,
                             String where, String order,
                             String group, String fieldNames) throws SOAPExceptionImpl {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SAPTableEntity tableEntity = tableRepository.findSAPTableEntityByAccessTokenAndParams(user.getAccessToken(), name, language, where, order, group, fieldNames);
            if (tableEntity == null || !tableEntity.isTableFull())
                throw new NullPointerException();

            return objectMapper.readValue(tableEntity.getSapTableJSON(), SAPTable.class);
        } catch (Exception ex) {
            LinkedHashMap<String, LinkedList<String>> dataset = getDataset(user, name, null, language,
                    where, order, group, fieldNames);
            SAPTable table = new SAPTable(dataset);

            CompletableFuture.runAsync(() -> saveTable(user, name, true, language, where, order, group, fieldNames, table));
            return table;
        }
    }

    public SAPTable getTable(AppUser user, String name, int offset, int count, Character language,
                             String where, String order,
                             String group, String fieldNames) throws SOAPExceptionImpl {
        ObjectMapper objectMapper = new ObjectMapper();
        SAPTableEntity tableEntity = null;
        try {
            tableEntity = tableRepository.findSAPTableEntityByAccessTokenAndParams(user.getAccessToken(), name, language, where, order, group, fieldNames);
            if (tableEntity == null || tableEntity.getRecordsCount() < offset + count && !tableEntity.isTableFull())
                throw new NullPointerException();

            SAPTable table = objectMapper.readValue(tableEntity.getSapTableJSON(), SAPTable.class);

            if (tableEntity.isTableFull() && table.getRecordsCount() < offset)
                return null;
            return table.getSubTable(offset, offset + count);
        } catch (Exception ex) {
            LinkedHashMap<String, LinkedList<String>> dataset = getDataset(user, name, offset + count + 1,
                    language, where, order, group, fieldNames);
            SAPTable table = new SAPTable(dataset);
            SAPTable subTable = table.getSubTable(offset, offset + count);

            if (tableEntity == null)
                CompletableFuture.runAsync(() -> saveTable(user, name, table.getRecordsCount() < offset + count + 1,
                        language, where, order, group, fieldNames, table));
            else {
                SAPTableEntity finalTableEntity = tableEntity;
                CompletableFuture.runAsync(() -> updateTable(finalTableEntity, table, table.getRecordsCount() < offset + count + 1));
            }
            return subTable;
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

    public LinkedHashMap<String, LinkedList<String>> getDataset(AppUser user,
                                                                String table, Integer recordsCount, Character language,
                                                                String where, String order,
                                                                String group, String fieldNames) throws SOAPExceptionImpl {
        String recordsCountStr = recordsCount != null ? String.valueOf(recordsCount) : null;
        language = (language != null && !language.equals(' ')) ? language : user.getLanguage();
        return datasetModule.requestDataSet(user.getSystem(),
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
