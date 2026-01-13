package io.spring.training.boot.server.services.implementations;

import io.spring.training.boot.server.DTOs.auth.RegisterRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.exceptions.DuplicateResourceException;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.services.AuthService;
import io.spring.training.boot.server.utils.mappers.AddressMapper;
import io.spring.training.boot.server.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto register(RegisterRequestDto requestDto) {
        if(userRepo.existsByUsername(requestDto.username())){
            throw new DuplicateResourceException("Username already exists");
        }
        if(userRepo.existsByEmail(requestDto.email())){
            throw new DuplicateResourceException("Email already exists");
        }
        User user = UserMapper.fromUserRequestDto(requestDto);
        UserAddress addresses = AddressMapper.fromAddressRequestDto(requestDto.address(), user);
        user.setAddress(addresses);
        String unencodedPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(unencodedPassword);
        user.setPassword(encodedPassword);
        return UserMapper.toUserResponseDto(userRepo.save(user));
    }


    @Override
    public String login(String email, String password) {
        return "";
    }
}
