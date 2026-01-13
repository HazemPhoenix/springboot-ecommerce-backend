package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.address.AddressRequestDto;
import io.spring.training.boot.server.DTOs.auth.RegisterRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.exceptions.DuplicateResourceException;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.services.implementations.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequestDto registerRequestDto;
    private User user;

    @BeforeEach
    public void setup(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("123 Main St", "City", "Country", "12345");

        registerRequestDto = new RegisterRequestDto(
                "testUser",
                "password123",
                "test@test.com",
                "201014656945",
                addressRequestDto
        );

        UserAddress address = UserAddress.builder()
                .id(1L)
                .street("123 Main St")
                .city("City")
                .country("Country")
                .zip("12345")
                .build();

        user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@test.com")
                .password("password123")
                .phoneNumber("201014656945")
                .address(address)
                .build();

        address.setUser(user);
    }

    @Test
    public void givenValidRegisterRequestDto_whenRegisterIsCalled_thenReturnsCorrectUserResponseDto() {
        // Arrange
        when(userRepo.existsByUsername("testUser")).thenReturn(false);
        when(userRepo.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDto response = authService.register(registerRequestDto);

        // Assert
        verify(userRepo).existsByUsername("testUser");
        verify(userRepo).existsByEmail("test@test.com");
        verify(userRepo).save(any(User.class));

        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo("testUser");
        assertThat(response.email()).isEqualTo("test@test.com");
    }

    @Test
    public void givenDuplicateUsername_whenRegisterIsCalled_thenThrowDuplicateResourceException() {
        // Arrange
        when(userRepo.existsByUsername("testUser")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequestDto))
                .isExactlyInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists");

        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    public void givenDuplicateEmail_whenRegisterIsCalled_thenThrowDuplicateResourceException() {
        // Arrange
        when(userRepo.existsByUsername("testUser")).thenReturn(false);
        when(userRepo.existsByEmail("test@test.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequestDto))
                .isExactlyInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists");

        verify(userRepo, never()).save(any(User.class));
    }
}
