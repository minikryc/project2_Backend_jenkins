package com.eouil.bank.bankapi.controllers;

import com.eouil.bank.bankapi.dtos.requests.DepositRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.TransferRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.WithdrawRequestDTO;
import com.eouil.bank.bankapi.dtos.responses.TransactionResponseDTO;
import com.eouil.bank.bankapi.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
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
}
