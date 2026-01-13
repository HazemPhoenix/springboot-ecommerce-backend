package io.spring.training.boot.server.services.implementations;

import io.spring.training.boot.server.DTOs.auth.RegisterRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.exceptions.DuplicateResourceException;
import io.spring.training.boot.server.exceptions.UserNotFoundException;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.services.UserService;
import io.spring.training.boot.server.utils.mappers.AddressMapper;
import io.spring.training.boot.server.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto getUserProfile (){
        // TODO: get user id from security context
        Long userId = 17L;
        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return UserMapper.toUserResponseDto(user);
    }

    // TODO: implement another method to get user details by id for admin use
}
