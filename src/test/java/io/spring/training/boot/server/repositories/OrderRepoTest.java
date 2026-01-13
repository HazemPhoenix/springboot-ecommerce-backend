package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.*;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepoTest {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AuthorRepo authorRepo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private GenreRepo genreRepo;

    @Test
    public void givenOrderIdAndStatus_whenUpdateOrderStatusIsCalled_thenStatusIsUpdated() {
        // arrange
        User u = User.builder()
                .username("firstuser")
                .password("firstpass")
                .email("first@email.com").build();

        userRepo.save(u);

        Author a = Author.builder()
                .name("first author")
                .bio("first bio")
                .nationality("first nat")
                .genres(Set.of(new Genre("Horror")))
                .photo("first.png").build();

        authorRepo.save(a);

        Genre g = new Genre("Drama");
        genreRepo.save(g);

        Book b = Book.builder()
                .title("Book One")
                .description("Description One")
                .price(new BigDecimal("29.99"))
                .numberOfPages(300)
                .stock(10)
                .image("book-one.png")
                .genres(Set.of(g))
                .authors(Set.of(a))
                .build();

        bookRepo.save(b);

        Order order = Order.builder()
                .user(u)
                .status(OrderStatus.PROCESSING)
                .paymentMethod(PaymentMethod.COD)
                .date(LocalDate.now())
                .build();


        OrderItem oi = OrderItem.builder()
                .order(order)
                .book(b)
                .quantity(5)
                .totalItemPrice(BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(29.99)))
                .build();

        order.setOrderItems(Set.of(oi));
        order.setTotalAmount(oi.getTotalItemPrice());

        orderRepo.save(order);

        Long orderId = order.getId();
        OrderStatus newStatus = OrderStatus.ON_THE_WAY;

        // act
        orderRepo.updateOrderStatus(orderId, newStatus);

        // assert
        Order updatedOrder = orderRepo.findById(orderId).get();
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getStatus()).isEqualTo(newStatus);
    }
}
