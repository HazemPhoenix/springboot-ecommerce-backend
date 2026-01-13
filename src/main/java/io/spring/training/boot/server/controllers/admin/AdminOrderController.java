package io.spring.training.boot.server.controllers.admin;


import io.spring.training.boot.server.DTOs.order.OrderAdminResponseDto;
import io.spring.training.boot.server.DTOs.order.OrderSummaryDto;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;

    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status){
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrderById(orderId);
        return ResponseEntity.noContent().build();
    }
}
