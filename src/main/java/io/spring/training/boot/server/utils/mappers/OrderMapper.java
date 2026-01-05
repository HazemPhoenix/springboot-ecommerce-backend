package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.*;
import io.spring.training.boot.server.models.Order;

import java.util.List;

public class OrderMapper {
    public static OrderAdminResponseDto toOrderAdminResponseDto(Order order){
        UserResponseDto userResponseDto = UserMapper.toUserResponseDto(order.getUser());
        List<OrderItemResponseDto> orderItemResponseDtoList = order.getOrderItems().stream().map(OrderItemMapper::toOrderItemResponseDto).toList();
        return new OrderAdminResponseDto(order.getId(), userResponseDto, order.getStatus(), order.getPaymentMethod(), order.getTotalAmount(), order.getDate(), orderItemResponseDtoList);
    }

    public static OrderUserResponseDto toOrderUserResponseDto(Order order){
        List<OrderItemResponseDto> orderItemResponseDtoList = order.getOrderItems().stream().map(OrderItemMapper::toOrderItemResponseDto).toList();
        return new OrderUserResponseDto(order.getId(), order.getStatus(), order.getPaymentMethod(), order.getTotalAmount(), order.getDate(), orderItemResponseDtoList);
    }

    public static OrderSummaryDto toOrderSummaryDto(Order order){
        return new OrderSummaryDto(order.getId(), order.getStatus(), order.getPaymentMethod(), order.getTotalAmount(), order.getDate());
    }

    public static Order fromOrderRequestDto(OrderRequestDto orderRequestDto){
        return new Order(orderRequestDto.paymentMethod());
    }

    public static Order fromOrderUpdateRequestDto(OrderUpdateRequestDto orderUpdateRequestDto) {
        return new Order();
    }
}
