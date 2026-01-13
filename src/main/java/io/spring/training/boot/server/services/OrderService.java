package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.order.OrderRequestDto;
import io.spring.training.boot.server.DTOs.order.OrderSummaryDto;
import io.spring.training.boot.server.DTOs.order.OrderUpdateRequestDto;
import io.spring.training.boot.server.DTOs.order.OrderUserResponseDto;
import io.spring.training.boot.server.models.enums.OrderStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderUserResponseDto createOrder(OrderRequestDto orderRequestDto);

    @Nullable Page<OrderSummaryDto> getUserOrders(Pageable pageable);

    OrderUserResponseDto updateOrderById(Long orderId, OrderUpdateRequestDto orderRequestDto);

    void cancelOrderById(Long orderId);

    @Nullable OrderUserResponseDto getOrderById(Long orderId);

    void updateOrderStatus(Long orderId, OrderStatus status);
}
