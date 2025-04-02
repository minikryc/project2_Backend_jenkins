package com.eouil.bank.bankapi.services;

import com.eouil.bank.bankapi.domains.User;
import com.eouil.bank.bankapi.dtos.requests.JoinRequest;
import com.eouil.bank.bankapi.dtos.requests.LoginRequest;
import com.eouil.bank.bankapi.dtos.responses.JoinResponse;
import com.eouil.bank.bankapi.dtos.responses.LoginResponse;
import com.eouil.bank.bankapi.exceptions.DuplicateEmailException;
import com.eouil.bank.bankapi.repositories.UserRepository;
import com.eouil.bank.bankapi.utils.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public JoinResponse join(JoinRequest joinRequest) {
        if (userRepository.findByEmail(joinRequest.email).isPresent()) {
            throw new DuplicateEmailException();
        }

        String userId = UUID.randomUUID().toString();

        User user = new User();
        user.setUserId(userId);
        user.setName(joinRequest.name);
        user.setEmail(joinRequest.email);
        user.setPassword(passwordEncoder.encode(joinRequest.password)); // 비밀번호 암호화

        userRepository.save(user);

        return new JoinResponse(user.getUserId(), user.getName(), user.getEmail());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // 이메일 확인
        User user = userRepository.findByEmail(loginRequest.email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // 비밀번호 확인 (BCrypt 암호 비교)
        if (!passwordEncoder.matches(loginRequest.password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // JWT 생성
        String accessToken = JwtUtil.generateToken(user.getUserId());

        return new LoginResponse(accessToken);
    }

    public void logout(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("No token / Token expired");
        }
    }
}
