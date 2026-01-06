package io.spring.training.boot.server.DTOs.user;

public record UserSummaryAdminDto(Long id,
                                  String username,
                                  String email,
                                  String phone,
                                  boolean verified,
                                  boolean active) {
}
