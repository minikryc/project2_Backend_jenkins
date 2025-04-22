package com.eouil.bank.bankapi.controllers;

import com.eouil.bank.bankapi.dtos.requests.JoinRequest;
import com.eouil.bank.bankapi.dtos.responses.JoinResponse;
import com.eouil.bank.bankapi.dtos.requests.LoginRequest;
import com.eouil.bank.bankapi.dtos.responses.LoginResponse;
import com.eouil.bank.bankapi.dtos.responses.LogoutResponse;
import com.eouil.bank.bankapi.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<JoinResponse> join(@Valid @RequestBody JoinRequest joinRequest) {
        log.info("[POST /join] 회원가입 요청: {}", joinRequest);
        JoinResponse joinResponse = authService.join(joinRequest);
        log.info("[POST /join] 회원가입 완료: {}", joinResponse);
        return ResponseEntity.ok(joinResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("[POST /login] 로그인 요청: {}", loginRequest);
        LoginResponse loginResponse = authService.login(loginRequest);
        log.info("[POST /login] 로그인 성공: {}", loginResponse);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        log.info("[POST /refresh] 토큰 갱신 요청");
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);
        log.info("[POST /refresh] 새로운 accessToken 발급 성공");
        return ResponseEntity.ok(new LoginResponse(newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("Authorization") String token) {
        log.info("[POST /logout] 로그아웃 요청");
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        authService.logout(token);
        log.info("[POST /logout] 로그아웃 완료");

        return ResponseEntity.ok(new LogoutResponse("로그아웃 완료"));
    }
}
