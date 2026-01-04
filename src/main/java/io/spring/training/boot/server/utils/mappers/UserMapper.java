package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.AddressResponseDto;
import io.spring.training.boot.server.DTOs.UserRequestDto;
import io.spring.training.boot.server.DTOs.UserResponseDto;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.UserAddress;

import java.util.List;

public class UserMapper {
    public static UserResponseDto toUserResponseDto(User user) {
        List<AddressResponseDto> addressResponseDtos = user.getAddresses().stream().map(AddressMapper::toAddressResponseDto).toList();
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getPhoneNumber(), addressResponseDtos, user.isVerified());
    }

    public static User fromUserRequestDto(UserRequestDto userRequest) {
        return new User(userRequest.username(), userRequest.password(), userRequest.email(), userRequest.phone());
    }
}
