package com.eouil.bank.bankapi.domains;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Account {
    @Id
    @Column(length = 20)
    private String accountNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private BigDecimal balance;
    private LocalDateTime createdAt;

}
