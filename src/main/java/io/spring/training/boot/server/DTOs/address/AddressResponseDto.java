package io.spring.training.boot.server.DTOs.address;

public record AddressResponseDto(String street,
                                 String city,
                                 String country,
                                 String zip) {
}
