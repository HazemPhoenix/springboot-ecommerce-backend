package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.address.AddressRequestDto;
import io.spring.training.boot.server.DTOs.auth.RegisterRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.exceptions.DuplicateResourceException;
import io.spring.training.boot.server.exceptions.UserNotFoundException;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private RegisterRequestDto registerRequestDto;

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
    public void givenValidRequest_whenRegisterUserIsCalled_thenReturnUserResponseDto() {
        // Arrange
        when(userRepo.existsByUsername("testUser")).thenReturn(false);
        when(userRepo.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDto response = userService.registerUser(registerRequestDto);

        // Assert
        verify(userRepo).existsByUsername("testUser");
        verify(userRepo).existsByEmail("test@test.com");
        verify(userRepo).save(any(User.class));

        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo("testUser");
        assertThat(response.email()).isEqualTo("test@test.com");
    }

    @Test
    public void givenDuplicateUsername_whenRegisterUserIsCalled_thenThrowDuplicateResourceException() {
        // Arrange
        when(userRepo.existsByUsername("testUser")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(registerRequestDto))
                .isExactlyInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists");

        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    public void givenDuplicateEmail_whenRegisterUserIsCalled_thenThrowDuplicateResourceException() {
        // Arrange
        when(userRepo.existsByUsername("testUser")).thenReturn(false);
        when(userRepo.existsByEmail("test@test.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(registerRequestDto))
                .isExactlyInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists");

        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    public void givenLoggedInUser_whenGetUserProfileIsCalled_thenReturnUserResponseDto() {
        // Arrange
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));

        // Act
        UserResponseDto response = userService.getUserProfile();

        // Assert
        verify(userRepo).findById(anyLong());
        assertThat(response.username()).isEqualTo("testUser");
        assertThat(response.email()).isEqualTo("test@test.com");
    }

    @Test
    public void givenInvalidId_whenGetUserProfileIsCalled_thenThrowUserNotFoundException() {
        // Arrange
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserProfile())
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepo).findById(anyLong());
    }
}