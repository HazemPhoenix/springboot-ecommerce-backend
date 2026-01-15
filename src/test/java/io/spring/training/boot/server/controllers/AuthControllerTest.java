package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.address.AddressRequestDto;
import io.spring.training.boot.server.DTOs.auth.LoginRequestDto;
import io.spring.training.boot.server.DTOs.auth.LoginResponseDto;
import io.spring.training.boot.server.DTOs.auth.RegisterRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;
import io.spring.training.boot.server.security.filters.JwtFilter;
import io.spring.training.boot.server.security.services.JwtService;
import io.spring.training.boot.server.security.services.UserDetailsServiceImpl;
import io.spring.training.boot.server.services.AuthService;
import io.spring.training.boot.server.utils.mappers.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;


    private List<User> users;

    private final String baseUrl = "/api/v1/auth";

    @Test
    public void givenValidUserRequestDto_whenRegisterUserIsCalled_thenReturnsNoContentResponse() throws Exception {
        // arrange
        User newUser = User.builder()
                .id(3L)
                .username("newuser")
                .password("Password123!")
                .email("newemail@email.com")
                .phoneNumber("201014656947")
                .active(true)
                .verified(false)
                .build();

        UserAddress address = new UserAddress(1L, newUser, "123 Main St", "City", "State", "12345");
        newUser.setAddress(address);

        RegisterRequestDto request = new RegisterRequestDto(
                newUser.getUsername(),
                newUser.getPassword(),
                newUser.getEmail(),
                newUser.getPhoneNumber(),
                new AddressRequestDto("123 Main St", "City", "State", "12345"));

        UserResponseDto response = UserMapper.toUserResponseDto(newUser);

        doNothing().when(authService).register(any(RegisterRequestDto.class));

        // act and assert
        mockMvc.perform(post(baseUrl + "/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(authService).register(any(RegisterRequestDto.class));
    }

    @Test
    public void givenInvalidUserRequestDto_whenRegisterUserIsCalled_thenReturnsUnProcessableContent() throws Exception {
        // arrange
        RegisterRequestDto request = new RegisterRequestDto(
                "validusername",
                "password123", // no capital letter, no special char
                "valid@email.com",
                "201014656948",
                new AddressRequestDto("", "", "", "") // Invalid address
        );

        // act and assert
        mockMvc.perform(post(baseUrl + "/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent());

        verify(authService, never()).register(any(RegisterRequestDto.class));
    }

    @Test
    public void givenValidLoginRequestDto_whenLoginIsCalled_thenReturnsJwtToken() throws Exception {
        // arrange
        String email = "test@email.com";
        String password = "Password123!";
        LoginRequestDto request = new LoginRequestDto(email, password);
        String testToken = "asdhjkasdmnakdnajksndksadkjaskj";
        LoginResponseDto response = new LoginResponseDto(email, testToken);

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        // act and assert
        mockMvc.perform(post(baseUrl + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(response.email()))
                .andExpect(jsonPath("$.token").value(response.token()));

        verify(authService).login(request);
    }

    @Test
    public void givenInvalidLoginRequestDto_whenLoginUserIsCalled_thenReturnsUnProcessableContentResponse() throws Exception {
        // arrange
        String email = "test"; // invalid email
        String password = "123456789"; // invalid password
        LoginRequestDto request = new LoginRequestDto(email, password);

        String testToken = "asdhjkasdmnakdnajksndksadkjaskj";
        LoginResponseDto response = new LoginResponseDto(email, testToken);

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        // act and assert
        mockMvc.perform(post(baseUrl + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent());

        verify(authService, never()).login(any(LoginRequestDto.class));
    }

}
