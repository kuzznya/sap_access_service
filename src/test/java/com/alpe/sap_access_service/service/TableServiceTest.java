package com.alpe.sap_access_service.service;

import com.alpe.sap_access_service.model.SAPTable;
import com.alpe.sap_access_service.model.SAPTableEntity;
import com.alpe.sap_access_service.model.SAPTableRecord;
import com.alpe.sap_access_service.model.User;
import com.alpe.sap_access_service.service.sap_modules.get_data.DatasetModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TableServiceTest {

    @Autowired
    TableService tableService;

    @MockBean
    DatasetModule datasetModule;

    @Autowired
    TableRepository tableRepository;

    LinkedHashMap<String, LinkedList<String>> testTable;

    @BeforeEach
    void setUp() throws SOAPExceptionImpl {
        tableRepository.deleteAll();

        testTable = new LinkedHashMap<>();
        LinkedList<String> col1 = new LinkedList<>();
        col1.add("test1");
        col1.add("test2");
        col1.add("test3");
        testTable.put("col1", col1);
        LinkedList<String> col2 = new LinkedList<>();
        col2.add("test4");
        col2.add("test5");
        col2.add("test6");
        testTable.put("col2", col2);
        LinkedList<String> col3 = new LinkedList<>();
        col3.add("test7");
        col3.add("test8");
        col3.add("test9");
        testTable.put("col3", col3);
        LinkedList<String> fieldNames = new LinkedList<>(testTable.keySet());
        testTable.put("fieldNames", fieldNames);
        testTable.put("repText", fieldNames);
        LinkedList<String> colLen = new LinkedList<>();
        colLen.add("1");
        colLen.add("1");
        colLen.add("1");
        testTable.put("columnLen", colLen);
        testTable.put("dataTypes", testTable.get("fieldNames"));
        testTable.put("domNames", testTable.get("fieldNames"));
        testTable.put("outputLen", colLen);
        testTable.put("decimals", testTable.get("fieldNames"));

        Mockito.when(datasetModule.getDataSet(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.eq("TEST"), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(testTable);
        Mockito.when(datasetModule.getDataSet(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.eq("WRONG_TEST"), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(SOAPExceptionImpl.class);
    }

    @Test
    void getTable() {
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(new User("TST", "u", "p"),
                "TEST", null, null, null, null, null), new SAPTable(testTable)));
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {}
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(new User("TST", "u", "p"),
                "TEST", null, null, null, null, null), new SAPTable(testTable)));

        assertThrows(SOAPExceptionImpl.class, () -> tableService.getTable(new User("TST", "u", "p"),
                "WRONG_TEST", null, null, null, null, null));
    }

    @Test
    void getSubTable() {
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(new User("TST", "u", "p"),
                "TEST", 1, 1, null, null, null, null, null), new SAPTable(testTable).getSubTable(1, 2)));
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {}
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(new User("TST", "u", "p"),
                "TEST", 1, 1, null, null, null, null, null), new SAPTable(testTable).getSubTable(1, 2)));

        assertDoesNotThrow(() -> assertEquals(tableService.getTable(new User("TST", "u", "p"),
                "TEST", 1, 100, null, null, null, null, null), new SAPTable(testTable).getSubTable(1, 100)));
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(new User("TST", "u", "p"),
                "TEST", 0, 100, null, null, null, null, null), new SAPTable(testTable)));
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(new User("TST", "u", "p"),
                "TEST", 100, 1, null, null, null, null, null),
                new SAPTable(new SAPTable(testTable).getColumns())));
        assertThrows(SOAPExceptionImpl.class, () -> tableService.getTable(new User("TST", "u", "p"),
                "WRONG_TEST", 1, 1, null, null, null, null, null));
    }

    @Test
    void saveTable() throws JsonProcessingException {
        SAPTable table = new SAPTable(testTable);
        User u = new User("TST", "u", "p");
        tableService.saveTable(u, "TEST2",
                true, null, null, null, null, null, table);
        assertEquals(tableRepository.findSAPTableEntityByAccessTokenAndParams(u.getAccessToken(), "TEST2", null, null, null, null, null).getSapTableJSON(),
                new ObjectMapper().writeValueAsString(table));
    }

    @Test
    void updateTable() throws JsonProcessingException {
        SAPTableEntity entity = new SAPTableEntity();
        entity.setAccessToken("token");
        entity.setName("TEST");

        SAPTable table = new SAPTable(testTable);
        entity.setSapTableJSON(new ObjectMapper().writeValueAsString(table));
        tableRepository.save(entity);

        LinkedList<SAPTableRecord> records = new LinkedList<>();
        SAPTableRecord newRecord = new SAPTableRecord();
        newRecord.addData("col1", "test42");
        newRecord.addData("col2", "test42");
        newRecord.addData("col3", "test42");
        records.add(newRecord);
        table.addRecords(records);

        tableService.updateTable(entity, table, true);
        assertEquals(tableRepository.findSAPTableEntityByAccessTokenAndParams("token", entity.getName(), entity.getLanguage(), entity.getWhere(), entity.getOrder(), entity.getGroup(), entity.getFieldNames()).getSapTableJSON(), entity.getSapTableJSON());
    }

    @Test
    void getDataset() {
        assertDoesNotThrow(() -> assertEquals(tableService.getDataset(new User("TST", "u", "p"),
                "TEST", null,null, null, null, null, null), testTable));
        assertThrows(SOAPExceptionImpl.class, () -> tableService.getDataset(new User("TST", "u", "p"),
                "WRONG_TEST", null,null, null, null, null, null));
    }

    @Test
    void deleteOldTables() {
        SAPTableEntity entity1 = new SAPTableEntity();
        entity1.setAccessToken("token");
        entity1.setName("TEST1");
        entity1.setCreationDate(new Date(0));
        entity1.setUpdateDate(new Date(0));

        SAPTableEntity entity2 = new SAPTableEntity();
        entity2.setAccessToken("token");
        entity2.setName("TEST2");
        entity2.setCreationDate(new Date(0));
        entity2.setUpdateDate(new Date(0));

        SAPTableEntity entity3 = new SAPTableEntity();
        entity3.setAccessToken("token");
        entity3.setName("TEST3");
        entity3.setCreationDate(new Date());
        entity3.setUpdateDate(new Date());

        tableRepository.save(entity1);
        tableRepository.save(entity2);
        tableRepository.save(entity3);

        tableService.deleteOldTables();

        // TODO assert that tables are deleted
    }
}