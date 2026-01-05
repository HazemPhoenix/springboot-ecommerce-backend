package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.OrderRequestDto;
import io.spring.training.boot.server.DTOs.OrderResponseDto;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    @Nullable List<OrderResponseDto> getUserOrders();

    OrderResponseDto updateOrderById(Long orderId, OrderRequestDto orderRequestDto);

    void cancelOrderById(Long orderId);
}
