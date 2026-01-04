package io.spring.training.boot.server.DTOs;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record UserRequestDto(@NotBlank(message = "Username is required")
                             @Size(min = 6, max = 20, message = "Username should be between 6 and 20 characters long")
                             String username,
                             @NotBlank(message = "Password is required")
                             @Size(min = 8, max = 20, message = "Password should be between 8 and 20 characters long")
                             @Pattern(
                                     regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                                     message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character"
                             )
                             String password,
                             @NotBlank(message = "Email is required")
                             @Email(message = "Email must be valid")
                             String email,
                             @NotBlank(message = "Phone number is required")
                             @Pattern(regexp = "^\\d{12}$", message = "Phone number must be exactly 12 digits")
                             String phone,
                             @NotNull(message = "Address list is required")
                             @Size(min = 1, max = 5, message = "Address list must contain at least 1 address and a maximum of 5")
                             List<AddressRequestDto> addresses) {
}