package com.alpe.sap_access_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class SAPTableTest {

    LinkedHashMap<String, LinkedList<String>> dataset;
    @BeforeEach
    void setUp() {
        dataset = new LinkedHashMap<>();
        dataset.put("fieldNames", new LinkedList<>());
        dataset.put("repText", new LinkedList<>());
        dataset.put("columnLen", new LinkedList<>());
        dataset.put("dataTypes", new LinkedList<>());
        dataset.put("domNames", new LinkedList<>());
        dataset.put("outputLen", new LinkedList<>());
        dataset.put("decimals", new LinkedList<>());

        for (int i = 0; i < 20; i++) {
            LinkedList<String> col = new LinkedList<>();
            for (int j = 0; j < 300; j++) {
                col.add("test" + i * 300 + j);
            }
            dataset.put("col" + i, col);
            dataset.get("fieldNames").add("col" + i);
            dataset.get("repText").add("col" + i);
            dataset.get("columnLen").add("150");
            dataset.get("dataTypes").add("col" + i);
            dataset.get("domNames").add("col" + i);
            dataset.get("outputLen").add("150");
            dataset.get("decimals").add("col" + i);
        }
    }

    @Test
    void testGetSetDelete() {
        SAPTable table = new SAPTable();
        assertEquals(table.getRecords().size(), 0);
        assertEquals(table.getColumns().size(), 0);

        assertDoesNotThrow(() -> new SAPTable(dataset));
        table = new SAPTable(dataset);
        assertEquals(table.getColumnsCount(), 20);
        assertEquals(table.getRecordsCount(), 300);

        LinkedList<SAPTableColumn> columns = new LinkedList<>();
        for (SAPTableColumn col : table.getColumns()) {
            assertTrue(dataset.containsKey(col.getSystemName()));
            columns.add(col);
        }
        assertEquals(new SAPTable(columns).getColumns(), table.getColumns());
        table.setColumns(columns);
        assertEquals(table.getColumns(), columns);

        table = new SAPTable(dataset);
        int oldRecordsCount = table.getRecordsCount();
        table.deleteRecord(100);
        assertEquals(table.getRecordsCount(), oldRecordsCount - 1);

        oldRecordsCount--;
        table.deleteRecords(100, 110);
        assertEquals(table.getRecordsCount(), oldRecordsCount - 10);

        table.deleteRecords();
        assertEquals(table.getRecords().size(), 0);

        assertEquals(new SAPTable(columns), table);
    }

    @Test
    void getSubTable() {
        SAPTable table = new SAPTable(dataset);

        LinkedHashMap<String, LinkedList<String>> subDataset = new LinkedHashMap<>(dataset);
        for (String key : subDataset.keySet()) {
            if (!(key.equals("fieldNames") || key.equals("repText") || key.equals("columnLen") ||
                    key.equals("dataTypes") || key.equals("domNames") || key.equals("outputLen") || key.equals("decimals")))
                subDataset.put(key, new LinkedList<>(subDataset.get(key).subList(0, 100)));
        }

        assertEquals(table.getSubTable(0, 100), new SAPTable(subDataset));
    }

    @Test
    void addRecords() {
        SAPTable table = new SAPTable(dataset);
        LinkedList<SAPTableRecord> records = new LinkedList<>();
        SAPTableRecord newRecord = new SAPTableRecord();
        for (int i = 0; i < 20; i++) {
            newRecord.addData("col" + i, "test42");
        }
        records.add(newRecord);
        table.addRecords(records);
    }
}