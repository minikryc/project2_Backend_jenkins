package com.eouil.bank.bankapi.dtos.responses;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;

    // access + refresh 둘 다 있을 때
    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // accessToken만 있을 때 (refresh 후 응답용)
    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.refreshToken = null;
    }
}