package io.spring.training.boot.server.services;

import io.spring.training.boot.server.models.Order;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Order> findByUserId(Long userId);
}
