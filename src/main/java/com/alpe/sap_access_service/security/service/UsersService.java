package com.alpe.sap_access_service.security.service;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.security.dao.UserRepository;
import com.alpe.sap_access_service.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

@Service
public class UsersService {

    private AuthService authService;

    private UserRepository repository;

    public UsersService(@Autowired AuthService authService,
                        @Autowired UserRepository repository) {
        this.authService = authService;
        this.repository = repository;

        // Run deletion of inactive users on timer event
        (new Timer()).schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       deleteInactiveUsers();
                                   }
                               },
                1000 * 120, // Now users are checked every 120 seconds
                1000 * 120); // If there will be too many users, the period should be increased
    }

    public String createUser(String system, String username, String password) throws AccessDeniedException {
        return createUser(system, username, password, null);
    }

    public String createUser(String system, String username, String password, Character language) throws AccessDeniedException {
        User user = new User(system, username, password, language);
        repository.save(user);
        if (!authService.auth(user))
            throw new AccessDeniedException("Cannot authorize user in SAP");
        return user.getAccessToken();
    }

    public User getUser(String accessToken) throws NoSuchElementException {
        try {
            User user = repository.getUserByAccessToken(accessToken);
            user.setLastTimeAccessed(new Date());
            // Asynchronously update lastTimeAccessed field in DB
            CompletableFuture.runAsync(() -> repository.save(user));
            return user;
        } catch (Exception ex) {
            throw new NoSuchElementException("User not found");
        }
    }

    public void refreshUser(String accessToken) throws NoSuchElementException {
        User user = getUser(accessToken);
        // Update lastTimeAccessedField in DB
        user.setLastTimeAccessed(new Date());
        repository.save(user);
    }

    public void deleteUser(String accessToken) throws NoSuchElementException {
        // Find user with such access token and delete it
        try {
            repository.delete(repository.getUserByAccessToken(accessToken));
        } catch (Exception ex) {
            throw new NoSuchElementException();
        }
    }

    public void deleteInactiveUsers() {
        // Delete users that were inactive more seconds than TokenLifetime value
        repository.deleteInactiveUsers(SapAccessServiceApplication.getTokenLifetime());
    }

}
