package io.spring.training.boot.server.DTOs.user;

import io.spring.training.boot.server.DTOs.address.AddressResponseDto;
import io.spring.training.boot.server.models.enums.RoleType;

import java.util.Set;

public record UserDetailsAdminDto(Long id,
                                  String username,
                                  String email,
                                  String phoneNumber,
                                  boolean active,
                                  boolean verified,
                                  AddressResponseDto address,
                                  Set<RoleType> roles) {
}
