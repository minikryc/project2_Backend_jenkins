package com.eouil.bank.bankapi.services;

import com.eouil.bank.bankapi.domains.Account;
import com.eouil.bank.bankapi.domains.User;
import com.eouil.bank.bankapi.dtos.requests.CreateAccountRequest;
import com.eouil.bank.bankapi.dtos.responses.CreateAccountResponse;
import com.eouil.bank.bankapi.repositories.AccountRepository;
import com.eouil.bank.bankapi.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public CreateAccountResponse createAccount(CreateAccountRequest AccountRequest) {
        User user = userRepository.findById(AccountRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String accountNumber = generateUniqueAccountNumber();

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setUser(user);
        account.setBalance(AccountRequest.getBalance() != null ? AccountRequest.getBalance() : BigDecimal.ZERO);
        account.setCreatedAt(LocalDateTime.now());

        accountRepository.save(account);

        return new CreateAccountResponse(
                account.getAccountNumber(),
                user.getUserId(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
    private String generateUniqueAccountNumber() {
        String number;
        do {
            number = String.valueOf(10000000000000L + (long) (Math.random() * 89999999999999L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
