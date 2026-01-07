package io.spring.training.boot.server.services;

import io.spring.training.boot.server.DTOs.order.*;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.exceptions.InsufficientStockException;
import io.spring.training.boot.server.exceptions.OrderNotFoundException;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Order;
import io.spring.training.boot.server.models.OrderItem;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.repositories.OrderItemRepo;
import io.spring.training.boot.server.repositories.OrderRepo;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.services.implementations.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrderRepo orderRepo;
    @Mock
    private BookRepo bookRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private OrderItemRepo orderItemRepo;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Book book1;
    private Book book2;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(17L)
                .username("testUser")
                .email("test@mail.com")
                .build();

        book1 = Book.builder()
                .id(1L)
                .title("Book One")
                .price(BigDecimal.valueOf(10.00))
                .stock(10)
                .build();

        book2 = Book.builder()
                .id(2L)
                .title("Book Two")
                .price(BigDecimal.valueOf(20.00))
                .stock(5)
                .build();

        orderItem = OrderItem.builder()
                .id(100L)
                .book(book1)
                .quantity(2)
                .totalItemPrice(BigDecimal.valueOf(20.00))
                .build();

        order = Order.builder()
                .id(1L)
                .user(user)
                .date(LocalDate.now())
                .status(OrderStatus.PROCESSING)
                .paymentMethod(PaymentMethod.COD)
                .totalAmount(BigDecimal.valueOf(20.00))
                .orderItems(new HashSet<>(Set.of(orderItem)))
                .build();

        orderItem.setOrder(order);
    }

    @Test
    public void givenValidOrderRequest_whenCreateOrderIsCalled_thenReturnOrderUserResponseDto() {
        // Arrange
        List<OrderItemRequestDto> items = List.of(
                new OrderItemRequestDto(1L, 2), // cost 20, Book1 stock becomes 8
                new OrderItemRequestDto(2L, 1)  // cost 20, Book2 stock becomes 4
        );
        OrderRequestDto request = new OrderRequestDto(PaymentMethod.COD, items);

        when(userRepo.findById(17L)).thenReturn(Optional.of(user));
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepo.findById(2L)).thenReturn(Optional.of(book2));
        when(orderRepo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        OrderUserResponseDto response = orderService.createOrder(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.totalAmount()).isEqualTo(BigDecimal.valueOf(40.0));
        assertThat(response.status()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(book1.getStock()).isEqualTo(8);
        assertThat(book2.getStock()).isEqualTo(4);
        verify(bookRepo).saveAll(anyList());
        verify(bookRepo).findById(1L);
        verify(bookRepo).findById(2L);
        verify(orderRepo).save(any(Order.class));
    }

    @Test
    public void givenInsufficientStock_whenCreateOrderIsCalled_thenThrowInsufficientStockException() {
        // Arrange
        List<OrderItemRequestDto> items = List.of(
                new OrderItemRequestDto(1L, 11) // stock is only 10
        );
        OrderRequestDto request = new OrderRequestDto(PaymentMethod.COD, items);

        when(userRepo.findById(17L)).thenReturn(Optional.of(user));
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book1));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(InsufficientStockException.class);

        verify(bookRepo, never()).saveAll(anyList());
        verify(orderRepo, never()).save(any(Order.class));
    }

    @Test
    public void givenInvalidBookId_whenCreateOrderIsCalled_thenThrowBookNotFoundException() {
        // Arrange
        List<OrderItemRequestDto> items = List.of(new OrderItemRequestDto(10L, 1));
        OrderRequestDto request = new OrderRequestDto(PaymentMethod.COD, items);

        when(userRepo.findById(17L)).thenReturn(Optional.of(user));
        when(bookRepo.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepo, never()).saveAll(anyList());
        verify(orderRepo, never()).save(any(Order.class));
    }

    @Test
    public void whenGetUserOrdersIsCalled_thenReturnPageOfOrderSummaryDto() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        when(orderRepo.findByUserId(17L, pageable)).thenReturn(new PageImpl<>(List.of(order)));

        // Act
        Page<OrderSummaryDto> result = orderService.getUserOrders(pageable);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).totalAmount()).isEqualTo(BigDecimal.valueOf(20.00));
        verify(orderRepo).findByUserId(17L, pageable);
    }

    @Test
    public void givenValidId_whenGetOrderByIdIsCalled_thenReturnOrderUserResponseDto() {
        // Arrange
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        // Act
        OrderUserResponseDto response = orderService.getOrderById(1L);

        // Assert
        verify(orderRepo).findById(1L);
        assertThat(response).isNotNull();
        assertThat(response.totalAmount()).isEqualTo(BigDecimal.valueOf(20.00));
    }

    @Test
    public void givenInvalidId_whenGetOrderByIdIsCalled_thenThrowOrderNotFoundException() {
        // Arrange
        when(orderRepo.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.getOrderById(10L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    public void givenValidUpdate_whenUpdateOrderByIdIsCalled_thenUpdateItemsAndStocks() {
        // Arrange
        // Current order has 2 of Book 1 (stock: 10, but in a real setting it should be 8 since 2 are already ordered)
        // therefore I'll just pretend that the existing book1 now has 8
        book1.setStock(8);

        List<OrderItemRequestDto> newItems = List.of(
                new OrderItemRequestDto(1L, 5) // requesting 5 of Book 1
        );
        OrderUpdateRequestDto updateRequest = new OrderUpdateRequestDto(newItems);

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(userRepo.findById(17L)).thenReturn(Optional.of(user));

        // mock finding old items to return stock
        when(orderItemRepo.findAllByOrderId(1L)).thenReturn(List.of(orderItem));
        when(bookRepo.findAllById(anyList())).thenReturn(List.of(book1)); // book1, since it is the only book in the old order

        when(orderRepo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        OrderUserResponseDto response = orderService.updateOrderById(1L, updateRequest);

        // Assert
        // return 2 items -> Stock becomes 8 + 2 = 10.
        // then consume 5 items -> Stock becomes 10 - 5 = 5.
        assertThat(book1.getStock()).isEqualTo(5);
        assertThat(response.totalAmount()).isEqualTo(BigDecimal.valueOf(50.0)); // 5 * 10.00

        verify(orderItemRepo).deleteAll(anyList());
        verify(orderItemRepo).saveAll(anyList());
        verify(bookRepo).saveAll(anyList());
    }

    @Test
    public void givenInsufficientStockForUpdate_whenUpdateOrderByIdIsCalled_thenThrowInsufficientStockException() {
        // Arrange
        book1.setStock(8); // 2 in order, 8 (10 - 2) remaining

        // Request 15 items. (Return 2 -> 10 total. Need 15 -> fail)
        List<OrderItemRequestDto> newItems = List.of(new OrderItemRequestDto(1L, 15));
        OrderUpdateRequestDto updateRequest = new OrderUpdateRequestDto(newItems);

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepo.findAllByOrderId(1L)).thenReturn(List.of(orderItem));
        when(bookRepo.findAllById(anyList())).thenReturn(List.of(book1));

        // Act & Assert
        assertThatThrownBy(() -> orderService.updateOrderById(1L, updateRequest))
                .isExactlyInstanceOf(InsufficientStockException.class);

        verify(bookRepo, never()).save(any(Book.class));
        verify(bookRepo, never()).saveAll(anyList());
        verify(orderRepo, never()).save(any(Order.class));
        verify(orderItemRepo, never()).saveAll(anyList());

    }

    @Test
    public void givenValidId_whenCancelOrderByIdIsCalled_thenRestoreStockAndSetStatusCancelled() {
        // Arrange
        // Order has 2 of Book 1. so book 1 should have 8 in stock currently
        book1.setStock(8);
        // return 2 -> Stock becomes 10;
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(bookRepo.findAllById(anyList())).thenReturn(List.of(book1));

        // Act
        orderService.cancelOrderById(1L);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(book1.getStock()).isEqualTo(10);

        verify(bookRepo).saveAll(anyList());
        verify(orderItemRepo).deleteAll(anyList());
    }

    @Test
    public void givenInvalidId_whenCancelOrderByIdIsCalled_thenThrowOrderNotFoundException() {
        // Arrange
        when(orderRepo.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.cancelOrderById(10L))
                .isInstanceOf(OrderNotFoundException.class);
    }
}