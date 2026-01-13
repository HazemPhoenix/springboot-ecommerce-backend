package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDto> getUserProfile(){
        return ResponseEntity.ok(userService.getUserProfile());
    }
}
