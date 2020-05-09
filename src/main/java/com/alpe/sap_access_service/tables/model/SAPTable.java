package com.alpe.sap_access_service.tables.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.InvalidParameterException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
public class SAPTable {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter @Setter
    private Long id;

    @Getter
    private List<SAPTableColumn> columns = new LinkedList<>();
    @Getter
    private List<SAPTableRecord> records = new LinkedList<>();

    public SAPTable(Long id, LinkedHashMap<String, LinkedList<String>> dataset) {
        this(dataset);
        this.id = id;
    }

    // Create SAPTable from map of columns
    public SAPTable(LinkedHashMap<String, LinkedList<String>> dataset) {
        LinkedHashMap<String, LinkedList<String>> map = new LinkedHashMap<>(dataset);
        LinkedList<String> systemNames;
        LinkedList<String> textNames;
        LinkedList<String> columnLen;
        LinkedList<String> dataTypes;
        LinkedList<String> domNames;
        LinkedList<String> outputLen;
        LinkedList<String> decimals;

        // Columns info from SAP
        systemNames = map.get("fieldNames");
        map.remove("fieldNames");
        textNames = map.get("repText");
        map.remove("repText");
        columnLen = map.get("columnLen");
        map.remove("columnLen");
        dataTypes = map.get("dataTypes");
        map.remove("dataTypes");
        domNames = map.get("domNames");
        map.remove("domNames");
        outputLen = map.get("outputLen");
        map.remove("outputLen");
        decimals = map.get("decimals");
        map.remove("decimals");

        columns = new LinkedList<>();
        for (int i = 0; i < systemNames.size(); i++) {
            columns.add(new SAPTableColumn(systemNames.get(i), textNames.get(i),
                    columnLen.get(i), dataTypes.get(i), domNames.get(i), outputLen.get(i), decimals.get(i)));
        }

        int recordsCount = map.get(map.keySet().toArray()[0]).size();

        records = new LinkedList<>();
        for (int i = 0; i < recordsCount; i++) {
            records.add(new SAPTableRecord());
            for (String key : map.keySet()) {
                records.get(i).addData(key, map.get(key).get(i));
            }
        }
    }

    public SAPTable(List<SAPTableColumn> columns) {
        this.columns = columns;
        this.records = new LinkedList<>();
    }

    public SAPTable(Long id, List<SAPTableColumn> columns) {
        this(columns);
        this.id = id;
    }

    public void setColumns(List<SAPTableColumn> columns) {
        records.clear();
        this.columns = columns;
    }

    @JsonGetter("records_count")
    public int getRecordsCount() {
        return records.size();
    }

    @JsonGetter("columns_count")
    public int getColumnsCount() {
        return columns.size();
    }

    public void addRecords(List<SAPTableRecord> records) {
        for (SAPTableRecord rec : records) {
            for (SAPTableColumn col : columns) {
                if (!rec.getData().containsKey(col.getSystemName())) {
                    throw new InvalidParameterException();
                }
            }
        }
        this.records.addAll(records);
    }

    public void setRecords(List<SAPTableRecord> records) {
        deleteRecords();
        addRecords(records);
    }

    public List<SAPTableRecord> getRecords(int from, int to) {
        if (from > records.size())
            return null;
        return records.subList(from, Math.min(to, records.size()));
    }

    public SAPTable getSubTable(int recordsFrom, int recordsTo) {
        SAPTable subTable = new SAPTable(id, columns);
        List<SAPTableRecord> subRecords = getRecords(recordsFrom, recordsTo);
        if (subRecords == null)
            return subTable;
        subTable.setRecords(new LinkedList<>(subRecords));
        return subTable;
    }

    public void deleteRecord(int index) {
        records.remove(index);
    }

    public void deleteRecords(int from, int to) {
        records.removeAll(new LinkedList<>(records.subList(from, to)));
    }

    public void deleteRecords() {
        try {
            records.clear();
        } catch (Exception ignored) {}
    }
}
