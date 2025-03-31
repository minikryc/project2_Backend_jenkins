package com.eouil.bank.bankapi.dtos.requests;

import java.math.BigDecimal;

public class TransferRequestDTO {

    private Long fromAccountNumber;
    private Long toAccountNumber;
    private BigDecimal amount;
    private String memo;
}
