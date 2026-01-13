package io.spring.training.boot.server.controllers.admin;

import io.spring.training.boot.server.config.StorageProperties;
import io.spring.training.boot.server.models.*;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;
import io.spring.training.boot.server.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminOrderController.class)
public class AdminOrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageProperties storageProperties;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Order> orders;

    private final String baseUrl = "/api/v1/admin/orders";

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

    @Test
    public void givenValidOrderIdAndStatus_whenUpdateOrderStatusIsCalled_thenReturnsUpdatedOrder() throws Exception {
        // arrange
        Order order = orders.get(0);
        OrderStatus newStatus = OrderStatus.ON_THE_WAY;

        when(orderService.updateOrderStatus(order.getId(), newStatus)).thenReturn(
                Order.builder()
                        .id(order.getId())
                        .user(order.getUser())
                        .status(newStatus)
                        .paymentMethod(order.getPaymentMethod())
                        .orderItems(order.getOrderItems())
                        .totalAmount(order.getTotalAmount())
                        .date(order.getDate())
                        .build()
        );

        // act and assert
        mockMvc.perform(patch(baseUrl + "/{orderId}", order.getId())
                    .param("status", newStatus.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.status").value(newStatus.name()));

        verify(orderService).updateOrderStatus(order.getId(), newStatus);
    }
}
