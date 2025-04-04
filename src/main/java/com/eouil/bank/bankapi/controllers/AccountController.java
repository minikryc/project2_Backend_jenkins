package com.eouil.bank.bankapi.controllers;

import com.eouil.bank.bankapi.dtos.requests.CreateAccountRequest;
import com.eouil.bank.bankapi.dtos.responses.CreateAccountResponse;
import com.eouil.bank.bankapi.dtos.responses.GetMyAccountResponse;
import com.eouil.bank.bankapi.services.AccountService;

import jakarta.validation.Valid;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/accounts")
    public ResponseEntity<CreateAccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest createRequest,
            @RequestHeader("Authorization") String token) {
        
        return ResponseEntity.ok(accountService.createAccount(createRequest, token));
    }

    @GetMapping("/accounts/me")
    public ResponseEntity<java.util.List<GetMyAccountResponse>> getMyAccount(@RequestHeader("Authorization") String token) {
        List<GetMyAccountResponse> responses = accountService.getMyaccount(token);
        return ResponseEntity.ok(responses);
    }


}