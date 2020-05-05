package com.alpe.sap_access_service.apps.service;

import com.alpe.sap_access_service.apps.model.SAPApplication;
import com.alpe.sap_access_service.security.model.User;
import com.alpe.sap_access_service.sap.get_data.DatasetModule;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AvailableAppsServiceTest {

    @Autowired
    AvailableAppsService appsService;

    @MockBean
    DatasetModule datasetModule;

    @BeforeEach
    void setUp() throws SOAPExceptionImpl {
        LinkedHashMap<String, LinkedList<String>> map = new LinkedHashMap<>();
        LinkedList<String> apps = new LinkedList<>();
        apps.add("Incorrect formatted string");
        apps.add("001. App 1");
        apps.add("002. App 2");
        map.put("REPI2", apps);
        Mockito.when(datasetModule.getDataSet(Mockito.eq("CORRECT_SYS"), Mockito.eq("CORRECT_USERNAME"), Mockito.eq("CORRECT_PASSWORD"),
                Mockito.eq(" "), Mockito.eq(" "), Mockito.any(), Mockito.eq(" "), Mockito.eq(" "),
                Mockito.eq(" "), Mockito.eq(" "))).thenReturn(map);
    }

    @Test
    void getAvailableApplications() {
        LinkedList<SAPApplication> apps = new LinkedList<>();
        apps.add(new SAPApplication(1, "App 1", null, "/apps/1"));
        apps.add(new SAPApplication(2, "App 2", null, "/apps/2"));
        assertDoesNotThrow(()-> assertEquals(appsService.getAvailableApplications(new User("CORRECT_SYS", "CORRECT_USERNAME", "CORRECT_PASSWORD")), apps));
    }
}