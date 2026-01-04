package io.spring.training.boot.server.services.implementations;

import io.spring.training.boot.server.DTOs.UserRequestDto;
import io.spring.training.boot.server.DTOs.UserResponseDto;
import io.spring.training.boot.server.exceptions.DuplicateResourceException;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.services.UserService;
import io.spring.training.boot.server.utils.mappers.AddressMapper;
import io.spring.training.boot.server.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Override
    public UserResponseDto registerUser(UserRequestDto requestDto) {
        if(userRepo.existsByUsername(requestDto.username())){
            throw new DuplicateResourceException("Username already exists");
        }
        if(userRepo.existsByEmail(requestDto.email())){
            throw new DuplicateResourceException("Email already exists");
        }
        User user = UserMapper.fromUserRequestDto(requestDto);
        List<UserAddress> addresses = requestDto.addresses().stream().map(address ->
            AddressMapper.fromAddressRequestDto(address, user)
        ).toList();
        user.setAddresses(new HashSet<>(addresses));
        return UserMapper.toUserResponseDto(userRepo.save(user));
    }
}
