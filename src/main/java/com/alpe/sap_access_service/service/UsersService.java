package com.alpe.sap_access_service.service;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.model.User;
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

    @Autowired
    private UserRepository userRepository;

    public UsersService(@Autowired AuthService authService) {
        this.authService = authService;

        // Run deletion of inactive users on timer event
        (new Timer()).schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       deleteInactiveUsers();
                                   }
                               }, 1000 * SapAccessServiceApplication.getTokenLifetime() / 4,
                1000 * SapAccessServiceApplication.getTokenLifetime() / 4);
    }

    public String createUser(String system, String username, String password) throws AccessDeniedException {
        return createUser(system, username, password, null);
    }

    public String createUser(String system, String username, String password, Character language) throws AccessDeniedException {
        User user = new User(system, username, password, language);
        userRepository.save(user);
        if (!authService.auth(user))
            throw new AccessDeniedException("Cannot authorize user in SAP");
        return user.getAccessToken();
    }

    public User getUser(String accessToken) {
        try {
            User user = userRepository.getUserByAccessToken(accessToken);
            user.setLastTimeAccessed(new Date());
            CompletableFuture.runAsync(() -> userRepository.save(user));
            return user;
        } catch (Exception ex) {
            return null;
        }
    }

    public void refreshUser(String accessToken) {
        User user = getUser(accessToken);
        if (user != null) {
            user.setLastTimeAccessed(new Date());
            userRepository.save(user);
        }
        else
            throw new NoSuchElementException();
    }

    public void deleteUser(String accessToken) {
        userRepository.delete(userRepository.getUserByAccessToken(accessToken));
    }

    public void deleteInactiveUsers() {
        userRepository.deleteInactiveUsers(SapAccessServiceApplication.getTokenLifetime());
    }

}
