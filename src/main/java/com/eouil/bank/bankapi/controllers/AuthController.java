package com.eouil.bank.bankapi.controllers;

import com.eouil.bank.bankapi.dtos.requests.JoinRequest;
import com.eouil.bank.bankapi.dtos.responses.JoinResponse;
import com.eouil.bank.bankapi.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController (AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/join")
    public ResponseEntity<JoinResponse> join(@RequestBody JoinRequest joinRequest) {
        JoinResponse joinResponse = authService.join(joinRequest);
        return ResponseEntity.ok(joinResponse);
    }
}
