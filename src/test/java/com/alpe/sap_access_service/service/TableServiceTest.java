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
        testTable.put("fieldNames", new LinkedList<>());
        testTable.put("repText", new LinkedList<>());
        testTable.put("columnLen", new LinkedList<>());
        testTable.put("dataTypes", new LinkedList<>());
        testTable.put("domNames", new LinkedList<>());
        testTable.put("outputLen", new LinkedList<>());
        testTable.put("decimals", new LinkedList<>());

        for (int i = 0; i < 20; i++) {
            LinkedList<String> col = new LinkedList<>();
            for (int j = 0; j < 150; j++) {
                col.add("test" + i * 150 + j);
            }
            testTable.put("col" + i, col);
            testTable.get("fieldNames").add("col" + i);
            testTable.get("repText").add("col" + i);
            testTable.get("columnLen").add("150");
            testTable.get("dataTypes").add("col" + i);
            testTable.get("domNames").add("col" + i);
            testTable.get("outputLen").add("150");
            testTable.get("decimals").add("col" + i);
        }

        Mockito.when(datasetModule.getDataSet(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.eq("TEST"), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(testTable);
        Mockito.when(datasetModule.getDataSet(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.eq("WRONG_TEST"), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(SOAPExceptionImpl.class);
    }

    @Test
    void getTable() {
        User u = new User("TST", "u", "p");
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(u,
                "TEST", null, null, null, null, null), new SAPTable(testTable)));
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {}
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(u,
                "TEST", null, null, null, null, null), new SAPTable(testTable)));

        assertThrows(SOAPExceptionImpl.class, () -> tableService.getTable(u,
                "WRONG_TEST", null, null, null, null, null));
    }

    @Test
    void getSubTable() {
        User u = new User("TST", "u", "p");
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(u,
                "TEST", 1, 10, null, null, null, null, null), new SAPTable(testTable).getSubTable(1, 11)));
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {}
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(u,
                "TEST", 1, 10, null, null, null, null, null), new SAPTable(testTable).getSubTable(1, 11)));
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(u,
                "TEST", 1, 120, null, null, null, null, null), new SAPTable(testTable).getSubTable(1, 121)));

        assertDoesNotThrow(() -> assertEquals(tableService.getTable(u,
                "TEST", 1, 100, null, null, null, null, null), new SAPTable(testTable).getSubTable(1, 101)));
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(u,
                "TEST", 0, 150, null, null, null, null, null), new SAPTable(testTable)));
        assertDoesNotThrow(() -> assertEquals(tableService.getTable(u,
                "TEST", 150, 1, null, null, null, null, null),
                new SAPTable(new SAPTable(testTable).getColumns())));
        assertThrows(SOAPExceptionImpl.class, () -> tableService.getTable(u,
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
        for (int i = 0; i < 20; i++) {
            newRecord.addData("col" + i, "test42");
        }
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