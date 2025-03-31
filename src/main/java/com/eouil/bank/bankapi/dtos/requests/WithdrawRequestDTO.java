package com.eouil.bank.bankapi.dtos.requests;

import java.math.BigDecimal;

public class WithdrawRequestDTO {
    private Long fromAccountNumber;
    private BigDecimal amount;
}
