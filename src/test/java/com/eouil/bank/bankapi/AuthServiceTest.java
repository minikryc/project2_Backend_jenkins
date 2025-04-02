package com.eouil.bank.bankapi;


import com.eouil.bank.bankapi.domains.User;
import com.eouil.bank.bankapi.dtos.requests.JoinRequest;
import com.eouil.bank.bankapi.dtos.requests.LoginRequest;
import com.eouil.bank.bankapi.dtos.responses.JoinResponse;
import com.eouil.bank.bankapi.dtos.responses.LoginResponse;
import com.eouil.bank.bankapi.exceptions.DuplicateEmailException;
import com.eouil.bank.bankapi.repositories.UserRepository;
import com.eouil.bank.bankapi.services.AuthService;
import com.eouil.bank.bankapi.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @Test
    void join_success() {
        JoinRequest request = new JoinRequest();
        request.name = "김구름";
        request.email = "goorm@example.com";
        request.password = "goormpassword123";

        when(userRepository.findByEmail(request.email)).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        JoinResponse response = authService.join(request);

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getUserId());
        assertEquals(request.name, savedUser.getName());
        assertEquals(request.email, savedUser.getEmail());
        assertEquals(response.name, request.name);
        assertEquals(response.email, request.email);
    }

    @Test
    void join_duplicateEmail_shouldThrowException() {
        JoinRequest request = new JoinRequest();
        request.name = "김구름";
        request.email = "goorm@example.com";
        request.password = "goormpassword123";

        when(userRepository.findByEmail(request.email)).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateEmailException.class, () -> authService.join(request));
    }

    @Test
    void login_success() {
        // given
        LoginRequest request = new LoginRequest();
        request.email = "valid@example.com";
        request.password = "validPassword123";

        User mockUser = new User();
        mockUser.setUserId("test-user-id");
        mockUser.setEmail(request.email);
        mockUser.setPassword(request.password);

        when(userRepository.findByEmail(request.email)).thenReturn(Optional.of(mockUser));

        try (MockedStatic<JwtUtil> jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.generateToken("test-user-id")).thenReturn("mock-token");

            // when
            LoginResponse response = authService.login(request);

            // then
            assertEquals("mock-token", response.getAccessToken());
        }
    }

    @Test
    void login_wrongEmail_shouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.email = "noexist@example.com";
        request.password = "irrelevant";

        when(userRepository.findByEmail(request.email)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Email not found", ex.getMessage());
    }

    @Test
    void login_wrongPassword_shouldThrowException() {
        LoginRequest request = new LoginRequest();
        request.email = "valid@example.com";
        request.password = "wrongPassword";

        User mockUser = new User();
        mockUser.setUserId("test-user-id");
        mockUser.setEmail(request.email);
        mockUser.setPassword("correctPassword");

        when(userRepository.findByEmail(request.email)).thenReturn(Optional.of(mockUser));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Invalid password", ex.getMessage());
    }

    @Test
    void logout_nullToken_shouldThrowException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.logout(null));
        assertEquals("No token / Token expired", ex.getMessage());
    }
}
