package com.eouil.bank.bankapi.services;

import com.eouil.bank.bankapi.domains.User;
import com.eouil.bank.bankapi.dtos.requests.JoinRequest;
import com.eouil.bank.bankapi.dtos.responses.JoinResponse;
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
}
