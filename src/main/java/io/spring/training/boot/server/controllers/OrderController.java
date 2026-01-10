package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.order.OrderRequestDto;
import io.spring.training.boot.server.DTOs.order.OrderSummaryDto;
import io.spring.training.boot.server.DTOs.order.OrderUpdateRequestDto;
import io.spring.training.boot.server.DTOs.order.OrderUserResponseDto;
import io.spring.training.boot.server.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderUserResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto){
        OrderUserResponseDto orderResponseDto = orderService.createOrder(orderRequestDto);
        URI orderURI = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{orderId}")
                .build(orderResponseDto.id());
        return ResponseEntity.created(orderURI).body(orderResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<OrderSummaryDto>> getUserOrders(@PageableDefault(size = 20, sort = "date") Pageable pageable){
        return ResponseEntity.ok(orderService.getUserOrders(pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderUserResponseDto> getOrderDetails(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderUserResponseDto> updateOrder(@Valid @RequestBody OrderUpdateRequestDto orderRequestDto, @PathVariable Long orderId){
        OrderUserResponseDto orderResponseDto = orderService.updateOrderById(orderId, orderRequestDto);
        return ResponseEntity.ok(orderResponseDto);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrderById(orderId);
        return ResponseEntity.noContent().build();
    }
}
