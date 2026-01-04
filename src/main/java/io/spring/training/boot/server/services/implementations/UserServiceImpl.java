package io.spring.training.boot.server.services.implementations;

import io.spring.training.boot.server.DTOs.UserRequestDto;
import io.spring.training.boot.server.DTOs.UserResponseDto;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Override
    public UserResponseDto registerUser(UserRequestDto requestDto) {
        return null;
    }
}
