package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.OrderRequestDto;
import io.spring.training.boot.server.DTOs.OrderResponseDto;
import io.spring.training.boot.server.DTOs.OrderSummaryDto;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    @Nullable Page<OrderSummaryDto> getUserOrders(Pageable pageable);

    OrderResponseDto updateOrderById(Long orderId, OrderRequestDto orderRequestDto);

    void cancelOrderById(Long orderId);
}
