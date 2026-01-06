package io.spring.training.boot.server.services.implementations;

import io.spring.training.boot.server.DTOs.UserRequestDto;
import io.spring.training.boot.server.DTOs.UserResponseDto;
import io.spring.training.boot.server.exceptions.DuplicateResourceException;
import io.spring.training.boot.server.exceptions.UserNotFoundException;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.services.UserService;
import io.spring.training.boot.server.utils.mappers.AddressMapper;
import io.spring.training.boot.server.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Override
    @Transactional
    public UserResponseDto registerUser(UserRequestDto requestDto) {
        if(userRepo.existsByUsername(requestDto.username())){
            throw new DuplicateResourceException("Username already exists");
        }
        if(userRepo.existsByEmail(requestDto.email())){
            throw new DuplicateResourceException("Email already exists");
        }
        User user = UserMapper.fromUserRequestDto(requestDto);
        UserAddress addresses = AddressMapper.fromAddressRequestDto(requestDto.address(), user);
        user.setAddress(addresses);
        return UserMapper.toUserResponseDto(userRepo.save(user));
    }

    @Override
    public UserResponseDto getUserProfile(Long userId){
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return UserMapper.toUserResponseDto(user);
    }
}
