package com.eouil.bank.bankapi.dtos.responses;

import lombok.Getter;

@Getter
public class LoginResponse {
    public String accessToken;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}