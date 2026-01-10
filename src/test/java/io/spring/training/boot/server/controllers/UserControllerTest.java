package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.address.AddressRequestDto;
import io.spring.training.boot.server.DTOs.user.UserRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.models.*;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;
import io.spring.training.boot.server.services.OrderService;
import io.spring.training.boot.server.services.UserService;
import io.spring.training.boot.server.utils.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
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
    public void givenValidUserRequestDto_whenRegisterUserIsCalled_thenReturnsUserResponseDto() throws Exception {
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

        UserRequestDto request = new UserRequestDto(
                newUser.getUsername(),
                newUser.getPassword(),
                newUser.getEmail(),
                newUser.getPhoneNumber(),
                new AddressRequestDto("123 Main St", "City", "State", "12345"));

        UserResponseDto response = UserMapper.toUserResponseDto(newUser);

        when(userService.registerUser(any(UserRequestDto.class))).thenReturn(response);

        // act and assert
        mockMvc.perform(post("/" + baseUrl)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/" + baseUrl + "/3"))
                .andExpect(jsonPath("$.id").value(3L));

        verify(userService, times(1)).registerUser(any(UserRequestDto.class));
    }

    @Test
    public void givenInvalidUserRequestDto_whenRegisterUserIsCalled_thenReturnsUnProcessableContent() throws Exception {
        // arrange
        UserRequestDto request = new UserRequestDto(
                "validusername",
                "password123", // no capital letter, no special char
                "valid@email.com",
                "201014656948",
                new AddressRequestDto("", "", "", "") // Invalid address
        );

        // act and assert
        mockMvc.perform(post("/" + baseUrl)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent());

        verify(userService, never()).registerUser(any(UserRequestDto.class));
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
