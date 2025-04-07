package com.eouil.bank.bankapi.repositories;

import com.eouil.bank.bankapi.domains.Account;
import com.eouil.bank.bankapi.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Account a JOIN FETCH a.user WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUser(User user);
}
