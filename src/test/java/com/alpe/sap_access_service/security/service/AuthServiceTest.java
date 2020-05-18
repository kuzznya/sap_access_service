package com.alpe.sap_access_service.security.service;

import com.alpe.sap_access_service.user.model.User;
import com.alpe.sap_access_service.sap.get_data.DatasetModule;
import com.alpe.sap_access_service.user.service.AuthService;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @MockBean
    DatasetModule datasetModule;

    @BeforeEach
    void setUp() throws SOAPExceptionImpl {
        Mockito.when(datasetModule.getDataSet(Mockito.eq("CORRECT_SYS"), Mockito.eq("CORRECT_USERNAME"), Mockito.eq("CORRECT_PASSWORD"),
                Mockito.eq(" "), Mockito.eq(" "), Mockito.any(), Mockito.eq(" "), Mockito.eq(" "),
                Mockito.eq(" "), Mockito.eq(" "))).thenReturn(null);
        Mockito.when(datasetModule.getDataSet(Mockito.eq("WRONG_SYS"), Mockito.eq("WRONG_USERNAME"), Mockito.eq("WRONG_PASSWORD"),
                Mockito.eq(" "), Mockito.eq(" "), Mockito.any(), Mockito.eq(" "), Mockito.eq(" "),
                Mockito.eq(" "), Mockito.eq(" "))).thenThrow(new SOAPExceptionImpl());
    }

    @Test
    void auth() {
        assertTrue(authService.auth(new User("CORRECT_SYS", "CORRECT_USERNAME", "CORRECT_PASSWORD")));
        assertFalse(authService.auth(new User("WRONG_SYS", "WRONG_USERNAME", "WRONG_PASSWORD")));
    }
}