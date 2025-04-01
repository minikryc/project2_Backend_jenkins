package com.eouil.bank.bankapi.repositories;

import com.eouil.bank.bankapi.domains.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByAccountNumber(String accountNumber);
}
