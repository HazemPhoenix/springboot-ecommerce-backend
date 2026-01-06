package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.user.UserRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto requestDto){
        UserResponseDto responseDto = userService.registerUser(requestDto);
        URI userUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{userId}")
                .build(responseDto.id());
        return ResponseEntity.created(userUri).body(responseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
}
