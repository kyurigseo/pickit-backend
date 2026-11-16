package com.pickit.backend.auth.service;

import com.pickit.backend.auth.dto.request.SignupRequest;
import com.pickit.backend.auth.dto.response.SignupResponse;
import com.pickit.backend.auth.entity.AuthAccount;
import com.pickit.backend.auth.entity.AuthProvider;
import com.pickit.backend.auth.entity.User;
import com.pickit.backend.auth.repository.AuthAccountRepository;
import com.pickit.backend.auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, AuthAccountRepository authAccountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authAccountRepository = authAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public SignupResponse signup(SignupRequest request) {
        if (authAccountRepository.existsByProviderAndProviderUserId(AuthProvider.LOCAL, request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        User user = userRepository.save(new User(request.username()));
        authAccountRepository.save(new AuthAccount(
                user,
                AuthProvider.LOCAL,
                request.email(),
                passwordEncoder.encode(request.password())
        ));

        return new SignupResponse(user.getUserId());
    }
}
