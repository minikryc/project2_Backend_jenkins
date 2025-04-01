package com.eouil.bank.bankapi.dtos.responses;

public class JoinResponse {
    public Long userId;
    public String name;
    public String email;

    public JoinResponse(Long userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}
