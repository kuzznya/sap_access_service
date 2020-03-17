package com.alpe.sap_access_service.services;

import com.alpe.sap_access_service.model.AppUser;
import com.alpe.sap_access_service.model.SAPTableEntity;
import com.alpe.sap_access_service.services.sap_modules.get_data.DatasetModule;
import com.alpe.sap_access_service.model.SAPTable;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.LinkedList;


@Service
public class TableService {

    DatasetModule datasetModule;

    @Autowired
    private TableRepository tableRepository;

    public TableService(@Autowired DatasetModule datasetModule) {
        this.datasetModule = datasetModule;
    }

    public SAPTable getTable(AppUser user, String name, Integer recordsCount, Character language,
                             String where, String order,
                             String group, String fieldNames) throws SOAPExceptionImpl {
        Integer hash = (name + recordsCount + language + where + order + group + fieldNames).hashCode();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SAPTableEntity tableEntity = tableRepository.findSAPTableEntityByAccessTokenAndParamsHash(user.getAccessToken(), hash);
            if (tableEntity == null)
                throw new NullPointerException();
            return objectMapper.readValue(tableEntity.getSapTableJSON(), SAPTable.class);
        } catch (Exception ex) {
            LinkedHashMap<String, LinkedList<String>> dataset = getDataset(user, name, recordsCount, language,
                    where, order, group, fieldNames);
            SAPTable table = new SAPTable(dataset);
            SAPTableEntity entity = new SAPTableEntity();
            entity.setAccessToken(user.getAccessToken());
            entity.setParamsHash(hash);
            entity.setName(name);
            entity.setRecordsCount(recordsCount);
            entity.setLanguage(language);
            try {
                entity.setSapTableJSON(objectMapper.writeValueAsString(table));
                tableRepository.save(entity);
                return table;
            } catch (Exception ex2) {
                ex2.printStackTrace();
                return null;
            }
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

}
