package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.UserRequestDto;
import io.spring.training.boot.server.DTOs.UserResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponseDto registerUser(UserRequestDto requestDto);
}
