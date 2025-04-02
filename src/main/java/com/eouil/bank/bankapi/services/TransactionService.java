package com.eouil.bank.bankapi.services;

import com.eouil.bank.bankapi.domains.Account;
import com.eouil.bank.bankapi.domains.Transaction;
import com.eouil.bank.bankapi.domains.TransactionStatus;
import com.eouil.bank.bankapi.domains.TransactionType;
import com.eouil.bank.bankapi.dtos.requests.DepositRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.TransferRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.WithdrawRequestDTO;
import com.eouil.bank.bankapi.dtos.responses.TransactionResponseDTO;
import com.eouil.bank.bankapi.repositories.AccountRepository;
import com.eouil.bank.bankapi.repositories.TransactionJdbcRepository;
import com.eouil.bank.bankapi.repositories.UserRepository;
import com.eouil.bank.bankapi.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionJdbcRepository transactionRepository;

    public TransactionResponseDTO transfer(TransferRequestDTO request, String token) {
        String email = JwtUtil.validateTokenAndGetEmail(token);
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")).getUserId();

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("From Account not found"));
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("To Account not found"));

        if (!fromAccount.getUser().getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized access to account");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction tx = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .memo(request.getMemo())
                .status(TransactionStatus.COMPLETED)
                .balanceAfter(fromAccount.getBalance())
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);

        return buildResponse(tx);
    }

    public TransactionResponseDTO withdraw(WithdrawRequestDTO request, String token) {
        String email = JwtUtil.validateTokenAndGetEmail(token);
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")).getUserId();

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!fromAccount.getUser().getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized access to account");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(fromAccount);

        Transaction tx = Transaction.builder()
                .fromAccount(fromAccount)
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .memo("출금")
                .status(TransactionStatus.COMPLETED)
                .balanceAfter(fromAccount.getBalance())
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);

        return buildResponse(tx);
    }

    public TransactionResponseDTO deposit(DepositRequestDTO request, String token) {
        String email = JwtUtil.validateTokenAndGetEmail(token);
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")).getUserId();

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!toAccount.getUser().getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized access to account");
        }

        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(toAccount);

        Transaction tx = Transaction.builder()
                .toAccount(toAccount)
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .memo("입금")
                .status(TransactionStatus.COMPLETED)
                .balanceAfter(toAccount.getBalance())
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);

        return buildResponse(tx);
    }

    private TransactionResponseDTO buildResponse(Transaction tx) {
        return TransactionResponseDTO.builder()
                .transactionID(tx.getTransactionId())
                .fromAccountNumber(tx.getFromAccount() != null ? tx.getFromAccount().getAccountNumber() : null)
                .toAccountNumber(tx.getToAccount() != null ? tx.getToAccount().getAccountNumber() : null)
                .type(tx.getType().name())
                .amount(tx.getAmount())
                .memo(tx.getMemo())
                .status(tx.getStatus().name())
                .balanceAfter(tx.getBalanceAfter())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
