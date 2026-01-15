package io.spring.training.boot.server.DTOs.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(@NotBlank(message = "Email is required")
                              @Email(message = "Email must be valid")
                              String email,
                              @NotBlank(message = "Password is required")
                              @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters long")
                              @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                                      message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character")
                              String password) {
}
