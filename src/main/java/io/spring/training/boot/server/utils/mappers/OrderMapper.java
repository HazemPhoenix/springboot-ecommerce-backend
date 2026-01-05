package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.OrderItemResponseDto;
import io.spring.training.boot.server.DTOs.OrderRequestDto;
import io.spring.training.boot.server.DTOs.OrderResponseDto;
import io.spring.training.boot.server.DTOs.UserResponseDto;
import io.spring.training.boot.server.models.Order;
import io.spring.training.boot.server.models.OrderItem;
import org.aspectj.weaver.ast.Or;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderResponseDto toOrderResponseDto(Order order){
        UserResponseDto userResponseDto = UserMapper.toUserResponseDto(order.getUser());
        List<OrderItemResponseDto> orderItemResponseDtoList = order.getOrderItems().stream().map(OrderItemMapper::toOrderItemResponseDto).toList();
        return new OrderResponseDto(order.getId(), userResponseDto, order.getStatus(), order.getPaymentMethod(), order.getTotalAmount(), orderItemResponseDtoList);
    }

    public static Order fromOrderRequestDto(OrderRequestDto orderRequestDto){
        Set<OrderItem> orderItems = orderRequestDto.orderItems().stream().map(OrderItemMapper::fromOrderItemRequestDto).collect(Collectors.toSet());
        return new Order(orderRequestDto.paymentMethod(), orderItems);
    }
}
