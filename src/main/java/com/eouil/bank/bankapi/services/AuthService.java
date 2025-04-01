package com.eouil.bank.bankapi.services;

import com.eouil.bank.bankapi.domains.User;
import com.eouil.bank.bankapi.dtos.requests.JoinRequest;
import com.eouil.bank.bankapi.dtos.requests.LoginRequest;
import com.eouil.bank.bankapi.dtos.responses.JoinResponse;
import com.eouil.bank.bankapi.dtos.responses.LoginResponse;
import com.eouil.bank.bankapi.exceptions.DuplicateEmailException;
import com.eouil.bank.bankapi.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public JoinResponse join(JoinRequest joinRequest) {
        if (userRepository.findByEmail(joinRequest.email).isPresent()) {
            throw new DuplicateEmailException();
        }

        User user = new User();
        user.setName(joinRequest.name);
        user.setEmail(joinRequest.email);
        user.setPassword(joinRequest.password);
        userRepository.save(user);

        return new JoinResponse(user.getUserId(), user.getName(), user.getEmail());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // 이메일 확인
        User user = userRepository.findByEmail(loginRequest.email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // 비밀번호 확인
        if (!user.getPassword().equals(loginRequest.password)) {
            throw new RuntimeException("Invalid password");
        }

        // JWT 생성 (JWT 구현 후, 실제 값으로 교체해야 함)
        String accessToken = "jwt-access-token";
        String refreshToken = "jwt-refresh-token";

        return new LoginResponse(accessToken, refreshToken);
    }

    public void logout(String token) {
        // 실제로 JWT를 검증하고 로그아웃 처리 구현 해야 함
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("토큰 없음/만료");
        }
    }
}
