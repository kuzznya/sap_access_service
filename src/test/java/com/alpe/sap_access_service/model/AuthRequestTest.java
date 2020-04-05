package com.alpe.sap_access_service.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class AuthRequestTest {

    @Test
    void test() {
        AuthRequest request = new AuthRequest();
        request.setSystem("TST");
        assertEquals(request.getSystem(), "TST");
        request.setUsername("u");
        assertEquals(request.getUsername(), "u");
        request.setPassword("p");
        assertEquals(request.getPassword(), "p");
        request.setLanguage('R');
        assertEquals(request.getLanguage(), 'R');

        AuthRequest request1 = new AuthRequest("TST", "uu", "pp");
        AuthRequest request2 = new AuthRequest("TST", "uu", "pp", null);
        assertEquals(request1, request2);
    }
}