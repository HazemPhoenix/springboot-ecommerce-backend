package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.OrderRequestDto;
import io.spring.training.boot.server.DTOs.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto){
        OrderResponseDto orderResponseDto = orderRequestDto.createOrder(orderRequestDto);
        URI orderURI = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{orderId}")
                .build(orderResponseDto.id());
        return ResponseEntity.created(orderURI).body(orderResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(){
        return ResponseEntity.ok(orderService.getUserOrders());
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(@RequestBody OrderRequestDto orderRequestDto, @PathVariable Long orderId){
        OrderResponseDto orderResponseDto = orderService.updateOrderById(orderId, orderRequestDto);
        return ResponseEntity.ok(orderResponseDto);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrderById(orderId);
        return ResponseEntity.noContent().build();
    }
}
