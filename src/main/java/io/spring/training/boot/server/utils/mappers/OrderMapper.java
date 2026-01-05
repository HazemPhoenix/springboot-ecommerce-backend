package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.*;
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
        return new OrderResponseDto(order.getId(), userResponseDto, order.getStatus(), order.getPaymentMethod(), order.getTotalAmount(), order.getDate(), orderItemResponseDtoList);
    }

    public static OrderSummaryDto toOrderSummaryDto(Order order){
        return new OrderSummaryDto(order.getId(), order.getStatus(), order.getPaymentMethod(), order.getTotalAmount(), order.getDate());
    }

    public static Order fromOrderRequestDto(OrderRequestDto orderRequestDto){
        return new Order(orderRequestDto.paymentMethod());
    }
}
