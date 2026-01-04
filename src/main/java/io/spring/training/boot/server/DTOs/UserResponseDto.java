package io.spring.training.boot.server.DTOs;

import java.util.List;

public record UserResponseDto(Long id,
                              String username,
                              String email,
                              String phone,
                              AddressResponseDto address,
                              boolean verified) {
}
