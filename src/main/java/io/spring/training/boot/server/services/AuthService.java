package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.auth.RegisterRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;

public interface AuthService {
    UserResponseDto register(RegisterRequestDto request);
}
