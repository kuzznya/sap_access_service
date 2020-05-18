package com.alpe.sap_access_service.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String system;
    private String username;
    private String password;
    private Character language = null;

    public AuthRequest(String system, String username, String password) {
        this(system, username, password, null);
    }
}
