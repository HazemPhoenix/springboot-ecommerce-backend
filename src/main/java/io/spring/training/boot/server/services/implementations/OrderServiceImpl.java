package io.spring.training.boot.server.services.implementations;

import io.spring.training.boot.server.DTOs.*;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.exceptions.InsufficientStockException;
import io.spring.training.boot.server.exceptions.OrderNotFoundException;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Order;
import io.spring.training.boot.server.models.OrderItem;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.services.OrderRepo;
import io.spring.training.boot.server.services.OrderService;
import io.spring.training.boot.server.utils.mappers.OrderItemMapper;
import io.spring.training.boot.server.utils.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final BookRepo bookRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public OrderUserResponseDto createOrder(OrderRequestDto orderRequestDto) {
        // create the order object and set the status to PROCESSING
        Order order = OrderMapper.fromOrderRequestDto(orderRequestDto);
        order.setStatus(OrderStatus.PROCESSING);
        // TODO: the current user principal should be the order's user
        User user = userRepo.findById(17L).get();
        order.setUser(user);
        // set the date to today's
        order.setDate(LocalDate.now());
        // map the list of OrderItemDtos to OrderItems
        List<OrderItem> orderItems = constructOrderItems(orderRequestDto.orderItems(), order);
        // iterate over the list of items and check if the quantity is valid
        // update book quantities and calculate total amount
        BigDecimal totalAmount = processOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setOrderItems(new HashSet<>(orderItems));
        // save the order
        return OrderMapper.toOrderUserResponseDto(orderRepo.save(order));
    }

    private BigDecimal processOrderItems(List<OrderItem> orderItems){
        List<Book> updatedBooks = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(OrderItem orderItem : orderItems){
            Book book = orderItem.getBook();
            if(!validateQuantity(orderItem.getQuantity(), book.getStock())) {
                throw new InsufficientStockException("Required quantity (" + orderItem.getQuantity() + ") is more than the available stock (" + book.getStock() + ")");
            }
            book.setStock(book.getStock() - orderItem.getQuantity());
            updatedBooks.add(book);
            totalPrice = totalPrice.add(orderItem.getTotalItemPrice());
        }
        bookRepo.saveAll(updatedBooks);
        return totalPrice;
    }

    private List<OrderItem> constructOrderItems(List<OrderItemRequestDto> orderItemRequestDtos, Order order) {
        return orderItemRequestDtos.stream().map(orderItemRequestDto -> {
            Long bookId = orderItemRequestDto.bookId();
            Book book = bookRepo.findById(bookId).orElseThrow(() -> new BookNotFoundException("No book found with the id: " + bookId));
            OrderItem orderItem = OrderItemMapper.fromOrderItemRequestDto(orderItemRequestDto);
            orderItem.setBook(book);
            orderItem.setOrder(order);
            orderItem.setTotalItemPrice(book.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            System.out.println("order item price: " + orderItem.getTotalItemPrice());
            return orderItem;
            }).toList();
    }

    private boolean validateQuantity(int quantity, int availableStock) {
        return quantity <= availableStock;
    }

    @Override
    public @Nullable Page<OrderSummaryDto> getUserOrders(Pageable pageable) {
        // TODO: get the current principal's user id
        Long userId = 17L;
        return orderRepo.findByUserId(userId, pageable).map(OrderMapper::toOrderSummaryDto);
    }

    @Override
    public OrderUserResponseDto getOrderById(Long orderId){
        return orderRepo.findById(orderId)
                .map(OrderMapper::toOrderUserResponseDto)
                .orElseThrow(() -> new OrderNotFoundException("No order found with the id: " + orderId));
    }

    @Override
    public OrderUserResponseDto updateOrderById(Long orderId, OrderRequestDto orderRequestDto) {
        return null;
    }

    @Override
    public void cancelOrderById(Long orderId) {

    }
}
