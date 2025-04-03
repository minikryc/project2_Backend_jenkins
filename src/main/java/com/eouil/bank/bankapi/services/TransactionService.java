package com.eouil.bank.bankapi.services;

import com.eouil.bank.bankapi.domains.Account;
import com.eouil.bank.bankapi.domains.Transaction;
import com.eouil.bank.bankapi.domains.TransactionStatus;
import com.eouil.bank.bankapi.domains.TransactionType;
import com.eouil.bank.bankapi.domains.User;
import com.eouil.bank.bankapi.dtos.requests.DepositRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.TransferRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.WithdrawRequestDTO;
import com.eouil.bank.bankapi.dtos.responses.TransactionResponseDTO;
import com.eouil.bank.bankapi.repositories.AccountRepository;
import com.eouil.bank.bankapi.repositories.TransactionJdbcRepository;
import com.eouil.bank.bankapi.repositories.TransactionRepository;
import com.eouil.bank.bankapi.repositories.UserRepository;
import com.eouil.bank.bankapi.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionJdbcRepository transactionRepository;
    private final TransactionRepository transactionJPARepository;

    public TransactionResponseDTO transfer(TransferRequestDTO request, String token) {
        String userId = JwtUtil.validateTokenAndGetUserId(token);   // JWT 토큰에서 userId 추출

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("From Account not found"));
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("To Account not found"));

        if (!fromAccount.getUser().getUserId().equals(user.getUserId())) {
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
        String userId = JwtUtil.validateTokenAndGetUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!fromAccount.getUser().getUserId().equals(user.getUserId())) {
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
        String userId = JwtUtil.validateTokenAndGetUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!toAccount.getUser().getUserId().equals(user.getUserId())) {
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

    public List<TransactionResponseDTO> getTransactions(String token) {
        String userId = JwtUtil.validateTokenAndGetUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Account> accounts = accountRepository.findByUser(user);

        List<Transaction> allTransactions = new ArrayList<>();
        for (Account ac : accounts) {
            List<Transaction> transactions = transactionJPARepository.findByAccountNumber(ac.getAccountNumber());
            allTransactions.addAll(transactions);
        }

        return allTransactions.stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

}
