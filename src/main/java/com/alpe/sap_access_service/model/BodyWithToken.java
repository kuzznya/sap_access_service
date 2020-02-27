package com.alpe.sap_access_service.model;

public class BodyWithToken {

    private String access_token;

    public BodyWithToken() {}

    public BodyWithToken(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

}
