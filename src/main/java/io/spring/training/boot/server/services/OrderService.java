package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.OrderRequestDto;
import io.spring.training.boot.server.DTOs.OrderAdminResponseDto;
import io.spring.training.boot.server.DTOs.OrderSummaryDto;
import io.spring.training.boot.server.DTOs.OrderUserResponseDto;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderUserResponseDto createOrder(OrderRequestDto orderRequestDto);

    @Nullable Page<OrderSummaryDto> getUserOrders(Pageable pageable);

    OrderUserResponseDto updateOrderById(Long orderId, OrderRequestDto orderRequestDto);

    void cancelOrderById(Long orderId);

    @Nullable OrderUserResponseDto getOrderById(Long orderId);
}
