package io.spring.training.boot.server.DTOs;

public record AddressResponseDto(String street,
                                 String city,
                                 String country,
                                 String zip) {
}
