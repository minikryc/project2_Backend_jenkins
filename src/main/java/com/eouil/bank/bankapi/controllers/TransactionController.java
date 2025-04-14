package com.eouil.bank.bankapi.controllers;

import com.eouil.bank.bankapi.dtos.requests.DepositRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.TransactionHistoryRequest;
import com.eouil.bank.bankapi.dtos.requests.TransferRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.WithdrawRequestDTO;
import com.eouil.bank.bankapi.dtos.responses.TransactionHistoryResponse;
import com.eouil.bank.bankapi.dtos.responses.TransactionResponseDTO;
import com.eouil.bank.bankapi.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(
            @RequestBody TransferRequestDTO request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(transactionService.transfer(request, token));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @RequestBody WithdrawRequestDTO request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(transactionService.withdraw(request, token));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(
            @RequestBody DepositRequestDTO request,
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(transactionService.deposit(request, token));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.replace("Bearer ", ""); // 토큰 추출
        List<TransactionResponseDTO> transactions = transactionService.getTransactions(token);
        return ResponseEntity.ok(transactions);
    }
}
