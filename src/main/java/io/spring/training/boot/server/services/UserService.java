package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.UserRequestDto;
import io.spring.training.boot.server.DTOs.UserResponseDto;

public interface UserService {
    UserResponseDto registerUser(UserRequestDto requestDto);
}
