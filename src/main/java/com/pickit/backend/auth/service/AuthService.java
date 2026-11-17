package com.pickit.backend.auth.service;

import com.pickit.backend.auth.dto.request.LoginRequest;
import com.pickit.backend.auth.dto.request.SignupRequest;
import com.pickit.backend.auth.dto.response.LoginResponse;
import com.pickit.backend.auth.dto.response.SignupResponse;
import com.pickit.backend.auth.entity.AuthAccount;
import com.pickit.backend.auth.entity.AuthProvider;
import com.pickit.backend.auth.entity.RefreshToken;
import com.pickit.backend.auth.entity.User;
import com.pickit.backend.auth.repository.AuthAccountRepository;
import com.pickit.backend.auth.repository.RefreshTokenRepository;
import com.pickit.backend.auth.repository.UserRepository;
import com.pickit.backend.auth.util.JwtProvider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepository userRepository,
                       AuthAccountRepository authAccountRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.authAccountRepository = authAccountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
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

    public LoginResponse login(LoginRequest request) {
        AuthAccount authAccount = authAccountRepository.findByProviderAndProviderUserId(AuthProvider.LOCAL, request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.password(), authAccount.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        User user = authAccount.getUser();
        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        String tokenHash = encodeHash(refreshToken);
        refreshTokenRepository.save(new RefreshToken(
                user.getUserId(),
                tokenHash,
                LocalDateTime.now().plusNanos(jwtProvider.getRefreshTokenExpirationMillis() * 1000000L)
        ));

        return new LoginResponse(
                accessToken,
                refreshToken,
                new LoginResponse.UserSummary(
                        user.getUserId(),
                        user.getUsername(),
                        user.getProfileImageUrl(),
                        user.getMannerScore(),
                        user.getIsPremium()
                )
        );
    }

    private String encodeHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
