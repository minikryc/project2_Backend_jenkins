package com.eouil.bank.bankapi.dtos.responses;

public class LoginResponse {
    public String accessToken;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}