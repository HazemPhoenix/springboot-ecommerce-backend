package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.user.UserRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;

public interface UserService {
    UserResponseDto registerUser(UserRequestDto requestDto);

    UserResponseDto getUserProfile(Long userId);
}
