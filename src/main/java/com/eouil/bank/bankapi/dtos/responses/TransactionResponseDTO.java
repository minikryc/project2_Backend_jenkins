package com.eouil.bank.bankapi.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {
    private Long transactionID;
    private Long fromAccountNumber;
    private Long toAccountNumber;
    private String type;
    private BigDecimal amount;
    private String memo;
    private String status;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
}