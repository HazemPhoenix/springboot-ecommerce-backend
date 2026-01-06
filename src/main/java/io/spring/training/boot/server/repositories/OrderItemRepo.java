package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
//    @Query("delete from OrderItem oi where oi.order.id = :orderId")
//    @Modifying
//    void deleteOrderItemsByOrderId(Long orderId);

    @Query("select oi from OrderItem oi where oi.order.id = :orderId")
    List<OrderItem> findAllByOrderId(Long orderId);
}
