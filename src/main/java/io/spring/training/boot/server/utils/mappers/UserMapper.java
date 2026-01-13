package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.address.AddressResponseDto;
import io.spring.training.boot.server.DTOs.auth.RegisterRequestDto;
import io.spring.training.boot.server.DTOs.user.UserResponseDto;
import io.spring.training.boot.server.DTOs.user.UserSummaryAdminDto;
import io.spring.training.boot.server.models.User;

public class UserMapper {
    public static UserResponseDto toUserResponseDto(User user) {
        AddressResponseDto addressResponseDto = AddressMapper.toAddressResponseDto(user.getAddress());
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getPhoneNumber(), addressResponseDto, user.isVerified());
    }
    public static UserSummaryAdminDto toUserSummaryAdminDto(User user) {
        return new UserSummaryAdminDto(user.getId(), user.getUsername(), user.getEmail(), user.getPhoneNumber(), user.isVerified(), user.isActive());
    }

    public static User fromUserRequestDto(RegisterRequestDto userRequest) {
        return new User(userRequest.username(), userRequest.password(), userRequest.email(), userRequest.phone());
    }
}
