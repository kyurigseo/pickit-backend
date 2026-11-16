package com.pickit.backend.auth.controller;

import com.pickit.backend.auth.dto.request.SignupRequest;
import com.pickit.backend.auth.dto.response.SignupResponse;
import com.pickit.backend.auth.service.AuthService;
import com.pickit.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ApiResponse.success(response);
    }
}
