package com.alpe.sap_access_service.apps.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SAPApplicationTest {

    @Test
    public void test() {
        SAPApplication app = new SAPApplication();
        app.setId(1);
        assertEquals(app.getId(), 1);
        app.setName("App 1");
        assertEquals(app.getName(), "App 1");
        app.setDescription("Description");
        assertEquals(app.getDescription(), "Description");

        SAPApplication app1 = new SAPApplication(2, "App 2", "D");
        SAPApplication app2 = new SAPApplication(2, "App 2", "D", null);
        assertEquals(app1, app2);
    }

}