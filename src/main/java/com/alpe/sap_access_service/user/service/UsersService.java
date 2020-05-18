package com.alpe.sap_access_service.user.service;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.user.dao.UserRepository;
import com.alpe.sap_access_service.user.model.User;
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

    public String createUser(User user)
            throws AccessDeniedException {
        if (!authService.auth(user))
            throw new AccessDeniedException("Cannot authorize user in SAP");
        user.generateAccessToken();
        repository.save(user);
        return user.getAccessToken();
    }

    public User getUser(String accessToken) throws NoSuchElementException {
        try {
            User user = repository.findUserByAccessToken(accessToken);
            user.setLastTimeAccessed(new Date());
            // Asynchronously update lastTimeAccessed field in DB
            CompletableFuture.runAsync(() -> repository.save(user));
            return user;
        } catch (Exception ex) {
            throw new NoSuchElementException("User not found");
        }
    }

    public void deleteUser(User user) throws NoSuchElementException {
        // Find user with such access token and delete it
        if (!repository.existsById(user.getId()))
            throw new NoSuchElementException();
        repository.delete(user);
    }

    public void deleteInactiveUsers() {
        // Delete users that were inactive more seconds than TokenLifetime value
        repository.deleteInactiveUsers(SapAccessServiceApplication.getTokenLifetime());
    }

}
