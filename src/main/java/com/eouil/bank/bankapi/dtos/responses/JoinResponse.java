package com.eouil.bank.bankapi.dtos.responses;

public class JoinResponse {
    public String userId;
    public String name;
    public String email;

    public JoinResponse(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}
