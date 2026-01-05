package io.spring.training.boot.server.services;

import io.spring.training.boot.server.models.Order;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);
    Page<Order> findByUserId(Long userId, Pageable pageable);
}
