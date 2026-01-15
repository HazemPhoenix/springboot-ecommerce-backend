package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.auth.LoginRequestDto;
import io.spring.training.boot.server.DTOs.auth.LoginResponseDto;
import io.spring.training.boot.server.DTOs.auth.RegisterRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;

public interface AuthService {
    void register(RegisterRequestDto request);

    LoginResponseDto login(LoginRequestDto request);
}
