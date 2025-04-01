package com.eouil.bank.bankapi.dtos.responses;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class CreateAccountResponse {
    private String accountNumber;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public CreateAccountResponse(String accountNumber, Long userId, BigDecimal balance, LocalDateTime createdAt) {
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
        this.createdAt = createdAt;
    }
}
