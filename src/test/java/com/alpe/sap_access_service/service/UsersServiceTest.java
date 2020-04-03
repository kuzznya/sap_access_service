package com.alpe.sap_access_service.service;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsersServiceTest {

    @Autowired
    UsersService usersService;

    @MockBean
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        Mockito.when(authService.auth(Mockito.any())).thenReturn(true);
    }

    @Test
    void createUser() {
        String accessToken1 = usersService.createUser("SYS", "username", "password", 'R');
        String accessToken2 = usersService.createUser("SYS2", "u", "p", null);
        User user1 = userRepository.getUserByAccessToken(accessToken1);
        assertEquals(user1.getSystem(), "SYS");
        assertEquals(user1.getUsername(), "username");
        assertEquals(user1.getPassword(), "password");
        assertEquals(user1.getLanguage(),'R');
        User user2 = userRepository.getUserByAccessToken(accessToken2);
        assertEquals(user2.getSystem(), "SYS2");
        assertEquals(user2.getUsername(), "u");
        assertEquals(user2.getPassword(), "p");
        assertNull(user2.getLanguage());

        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {}

        assertDoesNotThrow(() -> usersService.createUser("SYS2", "u", "p", null));

        assertThrows(AssertionError.class, () -> usersService.createUser(null, "u", null, 'R'));
    }

    @Test
    void getUser() {
        User testUser = new User("TST", "user", "pass");
        userRepository.save(testUser);
        assertEquals(usersService.getUser(testUser.getAccessToken()), testUser);
        assertThrows(NoSuchElementException.class, () -> usersService.getUser("0000"));
    }

    @Test
    void refreshUser() {
        User testUser = new User("TST", "user", "pass");
        userRepository.save(testUser);
        assertDoesNotThrow(() -> usersService.refreshUser(testUser.getAccessToken()));
        assertThrows(NoSuchElementException.class, () -> usersService.refreshUser("0000"));
    }

    @Test
    void deleteUser() {
        User testUser = new User("TST", "user", "pass");
        userRepository.save(testUser);
        assertDoesNotThrow(() -> usersService.deleteUser(testUser.getAccessToken()));
        assertThrows(NoSuchElementException.class, () -> usersService.deleteUser(testUser.getAccessToken()));
    }

    @Test
    void deleteInactiveUsers() {
        User testUser1 = new User("TST", "u", "p", null);
        testUser1.setLastTimeAccessed(new Date(0));
        User testUser2 = new User("TST", "u2", "p2", null);
        testUser2.setLastTimeAccessed(new Date(0));
        User testUser3 = new User("TST", "u3", "p3", null);
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);
        usersService.deleteInactiveUsers();
        assertThrows(NoSuchElementException.class, () -> usersService.getUser(testUser1.getAccessToken()));
        assertThrows(NoSuchElementException.class, () -> usersService.getUser(testUser2.getAccessToken()));
        assertDoesNotThrow(() -> usersService.getUser(testUser3.getAccessToken()));
    }
}