package io.spring.training.boot.server.DTOs;

import jakarta.validation.constraints.NotBlank;

public record AddressRequestDto(@NotBlank(message = "Street is required")
                                String street,
                                @NotBlank(message = "City is required")
                                String city,
                                @NotBlank(message = "Country is required")
                                String country,
                                @NotBlank(message = "Zip code is required")
                                String zip) {
}
