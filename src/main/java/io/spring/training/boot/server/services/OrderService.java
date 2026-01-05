package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderUserResponseDto createOrder(OrderRequestDto orderRequestDto);

    @Nullable Page<OrderSummaryDto> getUserOrders(Pageable pageable);

    OrderUserResponseDto updateOrderById(Long orderId, OrderUpdateRequestDto orderRequestDto);

    void cancelOrderById(Long orderId);

    @Nullable OrderUserResponseDto getOrderById(Long orderId);
}
