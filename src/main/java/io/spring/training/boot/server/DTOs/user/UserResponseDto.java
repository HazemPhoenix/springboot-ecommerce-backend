package io.spring.training.boot.server.DTOs.user;

import io.spring.training.boot.server.DTOs.address.AddressResponseDto;

public record UserResponseDto(Long id,
                              String username,
                              String email,
                              String phone,
                              AddressResponseDto address,
                              boolean verified) {
}
