package com.eouil.bank.bankapi.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter@Setter
public class User {
    @Id @GeneratedValue
    private Long userId;
    @Column(length = 16, nullable = false)
    private String name;
    @Column(length = 50, nullable = false, unique = true)
    private String email;
    @Column(length = 100, nullable = false)
    private String password;
}
