package com.eouil.bank.bankapi.services;

import com.eouil.bank.bankapi.domains.User;
import com.eouil.bank.bankapi.dtos.requests.JoinRequest;
import com.eouil.bank.bankapi.dtos.requests.LoginRequest;
import com.eouil.bank.bankapi.dtos.responses.JoinResponse;
import com.eouil.bank.bankapi.dtos.responses.LoginResponse;
import com.eouil.bank.bankapi.exceptions.DuplicateEmailException;
import com.eouil.bank.bankapi.repositories.UserRepository;
import com.eouil.bank.bankapi.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // refresh token 임시 저장소
    final Map<String, String> refreshStore = new HashMap<>();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public JoinResponse join(JoinRequest joinRequest) {
        log.info("➡️ [JOIN] 요청 - email: {}", joinRequest.email);

        if (userRepository.findByEmail(joinRequest.email).isPresent()) {
            log.warn("[JOIN] 중복 이메일 시도 - {}", joinRequest.email);
            throw new DuplicateEmailException();
        }

        String userId = UUID.randomUUID().toString();

        User user = new User();
        user.setUserId(userId);
        user.setName(joinRequest.name);
        user.setEmail(joinRequest.email);
        user.setPassword(passwordEncoder.encode(joinRequest.password)); // 비밀번호 암호화

        userRepository.save(user);

        log.info("[JOIN] 완료 - userId: {}, email: {}", userId, user.getEmail());
        return new JoinResponse(user.getName(), user.getEmail());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        log.info("[LOGIN] 요청 - email: {}", loginRequest.email);

        User user = userRepository.findByEmail(loginRequest.email)
                .orElseThrow(() -> {
                    log.warn("[LOGIN] 존재하지 않는 이메일 - {}", loginRequest.email);
                    return new RuntimeException("Email not found");
                });

        if (!passwordEncoder.matches(loginRequest.password, user.getPassword())) {
            log.warn("[LOGIN] 비밀번호 불일치 - email: {}", loginRequest.email);
            throw new RuntimeException("Invalid password");
        }

        String accessToken = JwtUtil.generateAccessToken(user.getUserId());
        String refreshToken = JwtUtil.generateRefreshToken(user.getUserId());

        refreshStore.put(user.getUserId(), refreshToken);

        log.info("[LOGIN] 성공 - userId: {}", user.getUserId());
        return new LoginResponse(accessToken, refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {
        log.info("[REFRESH] 요청");

        String userId = JwtUtil.validateTokenAndGetUserId(refreshToken);
        String storedRefreshToken = refreshStore.get(userId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            log.warn("[REFRESH] 유효하지 않거나 만료된 토큰 - userId: {}", userId);
            throw new RuntimeException("Invalid or expired refresh token");
        }

        log.info("[REFRESH] accessToken 재발급 성공 - userId: {}", userId);
        return JwtUtil.generateAccessToken(userId);
    }

    public void logout(String token) {
        log.info("[LOGOUT] 요청");

        if (token == null || token.isEmpty()) {
            log.warn("[LOGOUT] 토큰 없음");
            throw new RuntimeException("No token / Token expired");
        }

        String userId = JwtUtil.validateTokenAndGetUserId(token);
        refreshStore.remove(userId);

        log.info("[LOGOUT] 완료 - userId: {}", userId);
    }

    // 테스트용
    public void putRefreshTokenForTest(String userId, String refreshToken) {
        refreshStore.put(userId, refreshToken);
    }
}
