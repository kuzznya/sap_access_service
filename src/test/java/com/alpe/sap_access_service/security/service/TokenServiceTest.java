package com.alpe.sap_access_service.security.service;

import com.alpe.sap_access_service.security.TokenService;
import com.alpe.sap_access_service.user.model.User;
import com.alpe.sap_access_service.user.dao.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.security.auth.login.CredentialException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void authenticate() {
        User testUser1 = new User("TST", "u", "p");
        userRepository.save(testUser1);
        assertDoesNotThrow(() -> assertEquals(tokenService.authenticate(testUser1.getAccessToken()).getUser(), testUser1));
        assertThrows(CredentialException.class, () -> tokenService.authenticate("0000"));
    }
}