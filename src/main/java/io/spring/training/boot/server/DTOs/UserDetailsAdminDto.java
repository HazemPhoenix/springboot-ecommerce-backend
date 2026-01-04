package io.spring.training.boot.server.DTOs;

import io.spring.training.boot.server.models.enums.RoleType;

import java.util.List;
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
