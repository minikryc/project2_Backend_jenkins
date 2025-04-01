package com.eouil.bank.bankapi.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter@Setter
public class CreateAccountRequest {
    private Long userId;
    private BigDecimal balance;
}
