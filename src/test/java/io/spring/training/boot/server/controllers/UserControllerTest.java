package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.models.*;
import io.spring.training.boot.server.security.filters.JwtFilter;
import io.spring.training.boot.server.security.services.JwtService;
import io.spring.training.boot.server.security.services.UserDetailsServiceImpl;
import io.spring.training.boot.server.services.UserService;
import io.spring.training.boot.server.utils.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private List<User> users;

    private final String baseUrl = "/api/v1/users";



    @BeforeEach
    public void setup(){
        User u1 = User.builder()
                .id(1L)
                .username("firstuser")
                .password("firstpass")
                .email("first@email.com")
                .active(true)
                .verified(false)
                .phoneNumber("201014656945")
                .build();

        UserAddress a1 = new UserAddress(1L, u1, "456 St", "city 1", "country 1", "54321");
        u1.setAddress(a1);

        User u2 = User.builder()
                .id(2L)
                .username("seconduser")
                .password("secondpass")
                .email("second@email.com")
                .active(true)
                .verified(true)
                .phoneNumber("201014656946")
                .build();

        UserAddress a2 = new UserAddress(2L, u2, "789 St", "city 2", "country 2", "67890");
        u2.setAddress(a2);

        users = List.of(u1, u2);
    }

    @Test
    public void givenLoggedInUser_whenGetUserProfileIsCalled_thenReturnsUserResponseDto() throws Exception {
        // arrange
        User user = users.get(0);
        UserResponseDto response = UserMapper.toUserResponseDto(user);

        when(userService.getUserProfile()).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()));

        verify(userService).getUserProfile();
    }

}
