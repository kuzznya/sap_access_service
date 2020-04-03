package com.alpe.sap_access_service.security;

import com.alpe.sap_access_service.model.User;
import com.alpe.sap_access_service.service.UserRepository;
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
        assertDoesNotThrow(() -> assertEquals(tokenService.authenticate(testUser1.getAccessToken()).getPrincipal(), testUser1));
        assertThrows(CredentialException.class, () -> tokenService.authenticate("0000"));
    }
}