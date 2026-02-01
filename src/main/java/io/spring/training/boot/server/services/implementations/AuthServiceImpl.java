package io.spring.training.boot.server.services.implementations;

import io.spring.training.boot.server.DTOs.auth.LoginRequestDto;
import io.spring.training.boot.server.DTOs.auth.LoginResponseDto;
import io.spring.training.boot.server.DTOs.auth.RegisterRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.exceptions.DuplicateResourceException;
import io.spring.training.boot.server.exceptions.UnauthorizedException;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.security.services.JwtService;
import io.spring.training.boot.server.services.AuthService;
import io.spring.training.boot.server.utils.mappers.AddressMapper;
import io.spring.training.boot.server.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.CredentialException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public void register(RegisterRequestDto requestDto) {
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
        userRepo.save(user);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        String email = request.email();
        String password = request.password();
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            authenticationManager.authenticate(authToken);
            String jwtToken = jwtService.generateToken(email);
            return new LoginResponseDto(email, jwtToken);
        } catch (AuthenticationException exception){
            throw new UnauthorizedException("Email and/or password are incorrect.");
        }
    }
}
