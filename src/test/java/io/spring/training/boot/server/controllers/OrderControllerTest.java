package io.spring.training.boot.server.controllers;

import io.spring.training.boot.server.DTOs.order.*;
import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.exceptions.OrderNotFoundException;
import io.spring.training.boot.server.models.*;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;
import io.spring.training.boot.server.services.BookService;
import io.spring.training.boot.server.services.OrderService;
import io.spring.training.boot.server.utils.mappers.OrderItemMapper;
import io.spring.training.boot.server.utils.mappers.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Order> orders;

    private final String baseUrl = "/api/v1/orders";

    @BeforeEach
    public void setup(){
        User u1 = User.builder()
                .id(1L)
                .username("firstuser")
                .password("firstpass")
                .email("first@email.com").build();

        User u2 = User.builder()
                .id(2L)
                .username("seconduser")
                .password("secondpass")
                .email("second@email.com").build();

        Author a1 = Author.builder()
                .id(1L)
                .name("first author")
                .bio("first bio")
                .nationality("first nat")
                .genres(Set.of(new Genre("Horror")))
                .photo("first.png").build();

        Author a2 = Author.builder()
                .id(2L)
                .name("second author")
                .bio("second bio")
                .nationality("second nat")
                .genres(Set.of(new Genre("Drama")))
                .photo("second.png").build();

        Book b1 = Book.builder()
                .id(1L)
                .title("Book One")
                .description("Description One")
                .price(new BigDecimal("29.99"))
                .numberOfPages(300)
                .stock(10)
                .image("book-one.png")
                .authors(Set.of(a1))
                .genres(Set.of(new Genre("Horror")))
                .build();

        Book b2 = Book.builder()
                .id(2L)
                .title("Book Two")
                .description("Description Two")
                .price(new BigDecimal("39.99"))
                .numberOfPages(400)
                .stock(5)
                .image("book-two.png")
                .authors(Set.of(a2))
                .genres(Set.of(new Genre("Drama")))
                .build();

        Order o1 = null;
        OrderItem oi1 = OrderItem.builder()
                .id(1L)
                .order(o1)
                .book(b1)
                .quantity(5)
                .totalItemPrice(BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(29.99)))
                .build();

        o1 = Order.builder()
                .id(1L)
                .user(u1)
                .status(OrderStatus.PROCESSING)
                .paymentMethod(PaymentMethod.COD)
                .orderItems(Set.of(oi1))
                .totalAmount(oi1.getTotalItemPrice())
                .date(LocalDate.now())
                .build();

        Order o2 = null;
        OrderItem oi2 = OrderItem.builder()
                .id(2L)
                .order(o2)
                .book(b2)
                .quantity(2)
                .totalItemPrice(BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(39.99)))
                .build();

        o2 = Order.builder()
                .id(2L)
                .user(u2)
                .status(OrderStatus.PROCESSING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .orderItems(Set.of(oi2))
                .totalAmount(oi2.getTotalItemPrice())
                .date(LocalDate.now())
                .build();

        orders = List.of(o1, o2);
    }

    @Test
    public void givenValidOrderRequestDto_whenCreateOrderIsCalled_thenReturnsOrderUserResponseDto() throws Exception {
        // arrange
        Order order = orders.get(0);
        OrderRequestDto request = new OrderRequestDto(PaymentMethod.COD, order.getOrderItems().stream().map(oi -> new OrderItemRequestDto(oi.getBook().getId(), oi.getQuantity())).toList());
        OrderUserResponseDto response = OrderMapper.toOrderUserResponseDto(order);

        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(response);

        // act and assert
        mockMvc.perform(post(baseUrl)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(orderService).createOrder(any(OrderRequestDto.class));
    }

    @Test
    public void givenInvalidOrderRequestDto_whenCreateOrderIsCalled_thenReturnsUnProcessableContent() throws Exception {
        // arrange
        Order order = orders.get(0);
        OrderRequestDto request = new OrderRequestDto(null, order.getOrderItems().stream().map(oi -> new OrderItemRequestDto(oi.getBook().getId(), -1)).toList());


        // act and assert
        mockMvc.perform(post(baseUrl)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent());

        verify(orderService, never()).createOrder(any(OrderRequestDto.class));
    }

    @Test
    public void givenExistingUser_whenGetUserOrdersIsCalled_thenReturnsPaginatedOrderSummaryDtoList() throws Exception {
        // arrange
        Pageable pageable = PageRequest.of(1, 20);

        List<OrderSummaryDto> orderSummaryDtos = orders.
                stream().
                filter(order -> order.getUser().getId() == 1L).
                map(OrderMapper::toOrderSummaryDto).
                toList();

        Page<OrderSummaryDto> response = new PageImpl<>(orderSummaryDtos, pageable, orderSummaryDtos.size());

        when(orderService.getUserOrders(any(Pageable.class))).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(orderSummaryDtos.size()))
                .andExpect(jsonPath("$.content[0].id").value(orderSummaryDtos.get(0).id()));

        verify(orderService).getUserOrders(any(Pageable.class));
    }

    @Test
    public void givenValidOrderId_whenGetOrderDetailsIsCalled_thenReturnsOrderUserResponseDto() throws Exception {
        // arrange
        Order order = orders.get(0);
        OrderUserResponseDto response = OrderMapper.toOrderUserResponseDto(order);

        when(orderService.getOrderById(eq(order.getId()))).thenReturn(response);

        // act and assert
        mockMvc.perform(get(baseUrl + "/{orderId}", order.getId())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(orderService).getOrderById(eq(order.getId()));
    }

    @Test
    public void givenInvalidOrderId_whenGetOrderDetailsIsCalled_thenReturnsNotFound() throws Exception {
        // arrange
        Long invalidOrderId = 10L;

        when(orderService.getOrderById(eq(invalidOrderId))).thenThrow(OrderNotFoundException.class);

        // act and assert
        mockMvc.perform(get(baseUrl + "/{orderId}", invalidOrderId)
                .contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(invalidOrderId);
    }

    @Test
    public void givenValidOrderIdAndValidOrderUpdateRequestDto_whenUpdateOrderIsCalled_thenReturnsOrderUserResponseDto() throws Exception {
        // arrange
        Order order = orders.get(0);

        Order updatedOrder = Order.builder()
                .id(order.getId())
                .user(order.getUser())
                .status(OrderStatus.PROCESSING)
                .paymentMethod(order.getPaymentMethod())
                .orderItems(orders.get(1).getOrderItems())
                .totalAmount(orders.get(1).getTotalAmount())
                .date(order.getDate())
                .build();


        OrderUpdateRequestDto request = new OrderUpdateRequestDto(orders.get(1).getOrderItems().stream().map(oi -> new OrderItemRequestDto(oi.getBook().getId(), oi.getQuantity())).toList());
        OrderUserResponseDto response = OrderMapper.toOrderUserResponseDto(updatedOrder);

        when(orderService.updateOrderById(order.getId(), request))
                .thenReturn(response);

        // act and assert
        mockMvc.perform(put(baseUrl + "/{orderId}", order.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(orderService).updateOrderById(order.getId(), request);
    }

    // update order: invalid order id -> 404 Not Found
    @Test
    public void givenInvalidOrderIdAndValidOrderUpdateRequestDto_whenUpdateOrderIsCalled_thenReturnsNotFound() throws Exception {
        // arrange
        Long invalidOrderId = 10L;

        Order order = orders.get(0);
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(orders.get(1).getOrderItems().stream().map(oi -> new OrderItemRequestDto(oi.getBook().getId(), oi.getQuantity())).toList());

        when(orderService.updateOrderById(eq(invalidOrderId), any(OrderUpdateRequestDto.class)))
                .thenThrow(OrderNotFoundException.class);

        // act and assert
        mockMvc.perform(put(baseUrl + "/{orderId}", invalidOrderId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(orderService).updateOrderById(invalidOrderId, request);
    }

    @Test
    public void givenValidOrderIdAndInvalidOrderUpdateRequestDto_whenUpdateOrderIsCalled_thenReturnsUnprocessableContent() throws Exception {
        // arrange
        Order order = orders.get(0);
        OrderUpdateRequestDto request = new OrderUpdateRequestDto(List.of(new OrderItemRequestDto(-1L, -5)));

        // act and assert
        mockMvc.perform(put(baseUrl + "/{orderId}", order.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent());

        verify(orderService, never()).updateOrderById(anyLong(), any(OrderUpdateRequestDto.class));
    }

    @Test
    public void givenOrderId_whenCancelOrderIsCalled_thenReturnsNoContent() throws Exception {
        // arrange
        Order order = orders.get(0);

        doNothing().when(orderService).cancelOrderById(order.getId());

        // act and assert
        mockMvc.perform(delete(baseUrl + "/{orderId}", order.getId())
                .contentType("application/json"))
                .andExpect(status().isNoContent());

        verify(orderService).cancelOrderById(order.getId());
    }
}
