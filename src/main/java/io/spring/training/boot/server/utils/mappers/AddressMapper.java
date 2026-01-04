package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.AddressRequestDto;
import io.spring.training.boot.server.DTOs.AddressResponseDto;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;

public class AddressMapper {
    public static AddressResponseDto toAddressResponseDto(UserAddress address) {
        return new AddressResponseDto(address.getStreet(), address.getCity(), address.getCountry(), address.getZip());
    }

    public static UserAddress fromAddressRequestDto(AddressRequestDto addressRequest, User user) {
        return new UserAddress(user, addressRequest.street(), addressRequest.city(), addressRequest.country(), addressRequest.zip());
    }
}
