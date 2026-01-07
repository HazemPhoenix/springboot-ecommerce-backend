package io.spring.training.boot.server.repositories;

import io.spring.training.boot.server.models.*;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.models.enums.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@DataJpaTest
public class OrderItemRepoTest {
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private OrderItemRepo orderItemRepo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AuthorRepo authorRepo;
    @Autowired
    private GenreRepo genreRepo;

    @Test
    public void givenExistingOrdersAndOrderItems_whenFindAllByOrderIdIsCalled_thenReturnsItemsForThatOrderOnly(){
        // Arrange
        User user = userRepo.save(User.builder().username("test").email("test@test.com").password("test").build());

        Genre g = new Genre("Horror");
        Genre genre = genreRepo.save(g);

        Author a = Author.builder().name("test").bio("test").nationality("test").photo("test").genres(Set.of(genre)).build();
        Author author = authorRepo.save(a);

        Book b1 = Book.builder()
                .title("book 1")
                .description("test")
                .price(BigDecimal.TEN)
                .numberOfPages(300)
                .stock(20)
                .genres(Set.of(genre))
                .authors(Set.of(author))
                .image("test.png").build();

        Book book1 = bookRepo.save(b1);

        Book b2 = Book.builder()
                .title("book 2")
                .description("test")
                .price(BigDecimal.TEN)
                .numberOfPages(300)
                .stock(20)
                .genres(Set.of(genre))
                .authors(Set.of(author))
                .image("test.png").build();

        Book book2 = bookRepo.save(b2);

        Order order1 = Order.builder().user(user).status(OrderStatus.PROCESSING).paymentMethod(PaymentMethod.COD).build();
        OrderItem oi1 = OrderItem.builder().order(order1).book(book1).quantity(3).totalItemPrice(book1.getPrice().multiply(BigDecimal.valueOf(3))).build();
        OrderItem oi2 = OrderItem.builder().order(order1).book(book2).quantity(1).totalItemPrice(book2.getPrice().multiply(BigDecimal.valueOf(1))).build();
        order1.setOrderItems(Set.of(oi1, oi2));
        order1.setTotalAmount(oi1.getTotalItemPrice().add(oi2.getTotalItemPrice()));
        order1.setDate(LocalDate.now());
        Order savedOrder1 = orderRepo.save(order1);

        Order order2= Order.builder().user(user).status(OrderStatus.PROCESSING).paymentMethod(PaymentMethod.COD).build();
        OrderItem oi3 = OrderItem.builder().order(order2).book(book2).quantity(5).totalItemPrice(book2.getPrice().multiply(BigDecimal.valueOf(5))).build();
        order2.setOrderItems(Set.of(oi3));
        order2.setTotalAmount(oi3.getTotalItemPrice());
        order2.setDate(LocalDate.now());
        Order savedOrder2 = orderRepo.save(order2);

        // Act
        List<OrderItem> orderItemList = orderItemRepo.findAllByOrderId(savedOrder1.getId());
        orderItemList.forEach(System.out::println);

        // Assert
        assertThat(orderItemList.size()).isEqualTo(2);
        assertThat(orderItemList.stream().map(OrderItem::getBook).toList()).contains(book1, book2);
        assertThat(orderItemList.stream().map(OrderItem::getTotalItemPrice).reduce(BigDecimal.ZERO, BigDecimal::add)).isEqualTo(order1.getTotalAmount());
    }

}
