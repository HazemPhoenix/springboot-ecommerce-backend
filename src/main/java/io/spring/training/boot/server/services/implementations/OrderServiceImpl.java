package io.spring.training.boot.server.services.implementations;

import io.spring.training.boot.server.DTOs.order.*;
import io.spring.training.boot.server.exceptions.BookNotFoundException;
import io.spring.training.boot.server.exceptions.InsufficientStockException;
import io.spring.training.boot.server.exceptions.OrderNotFoundException;
import io.spring.training.boot.server.models.Book;
import io.spring.training.boot.server.models.Order;
import io.spring.training.boot.server.models.OrderItem;
import io.spring.training.boot.server.models.User;
import io.spring.training.boot.server.models.enums.OrderStatus;
import io.spring.training.boot.server.repositories.BookRepo;
import io.spring.training.boot.server.repositories.OrderItemRepo;
import io.spring.training.boot.server.repositories.UserRepo;
import io.spring.training.boot.server.repositories.OrderRepo;
import io.spring.training.boot.server.security.CustomUserDetails;
import io.spring.training.boot.server.services.OrderService;
import io.spring.training.boot.server.utils.mappers.OrderItemMapper;
import io.spring.training.boot.server.utils.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final BookRepo bookRepo;
    private final UserRepo userRepo;
    private final OrderItemRepo orderItemRepo;

    @Override
    @Transactional
    public OrderUserResponseDto createOrder(OrderRequestDto orderRequestDto) {
        // create the order object and set the status to PROCESSING
        Order order = OrderMapper.fromOrderRequestDto(orderRequestDto);
        order.setStatus(OrderStatus.PROCESSING);
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepo.findById(userDetails.getId()).get();
        order.setUser(user);
        // set the date to today's
        order.setDate(LocalDate.now());
        // map the list of OrderItemDtos to OrderItems
        List<OrderItem> orderItems = constructOrderItems(orderRequestDto.orderItems(), order);
        BigDecimal totalAmount = processOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setOrderItems(new HashSet<>(orderItems));
        // save the order
        return OrderMapper.toOrderUserResponseDto(orderRepo.save(order));
    }

    private BigDecimal processOrderItems(List<OrderItem> orderItems){
        List<Book> updatedBooks = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        // iterate over the list of items and check if the quantity is valid
        // update book quantities and calculate total amount
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
                Book book = bookRepo.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
                OrderItem orderItem = OrderItemMapper.fromOrderItemRequestDto(orderItemRequestDto);
                orderItem.setBook(book);
                orderItem.setOrder(order);
                orderItem.setTotalItemPrice(book.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
                return orderItem;
            }).toList();
    }

    private boolean validateQuantity(int quantity, int availableStock) {
        return quantity <= availableStock;
    }

    @Override
    public @Nullable Page<OrderSummaryDto> getUserOrders(Pageable pageable) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        return orderRepo.findByUserId(userId, pageable).map(OrderMapper::toOrderSummaryDto);
    }

    @Override
    @PostAuthorize("returnObject.email() == principal.getUsername() or hasRole('ADMIN')")
    public OrderUserResponseDto getOrderById(Long orderId){
        return orderRepo.findById(orderId)
                .map(OrderMapper::toOrderUserResponseDto)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    @Transactional
    public OrderUserResponseDto updateOrderById(Long orderId, OrderUpdateRequestDto orderUpdateRequestDto) {
        Order oldOrder = orderRepo.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User owner = oldOrder.getUser();
        if(owner != null && owner.getId() != userDetails.getId()) {
            throw new AccessDeniedException("You are not authorized to update this order.");
        }

        if(oldOrder.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Cannot update the order since it has already been processed.");
        }

        Order newOrder = OrderMapper.fromOrderUpdateRequestDto(orderUpdateRequestDto);

        newOrder.setId(orderId);
        newOrder.setStatus(OrderStatus.PROCESSING);
        newOrder.setPaymentMethod(oldOrder.getPaymentMethod());

        List<OrderItem> orderItems = updateItemsForOrder(newOrder, orderUpdateRequestDto.orderItems());

        newOrder.setOrderItems(new HashSet<>(orderItems));

        User user = userRepo.findById(userDetails.getId()).get();
        newOrder.setUser(user);
        newOrder.setDate(oldOrder.getDate());

        BigDecimal totalAmount = orderItems.stream().map(OrderItem::getTotalItemPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        newOrder.setTotalAmount(totalAmount);

        return OrderMapper.toOrderUserResponseDto(orderRepo.save(newOrder));
    }

    public List<OrderItem> updateItemsForOrder(Order newOrder, List<OrderItemRequestDto> orderItemRequests){
        List<OrderItem> oldOrderItems = orderItemRepo.findAllByOrderId(newOrder.getId());
        List<Long> oldBookIds = oldOrderItems.stream().mapToLong(oi -> oi.getBook().getId()).boxed().toList();
        List<Book> oldBooks = bookRepo.findAllById(oldBookIds);
        List<Book> updatedBooks = returnBookStocks(oldOrderItems, oldBooks);
        orderItemRepo.deleteAll(oldOrderItems);

        List<OrderItem> newOrderItems = new ArrayList<>();
        for(OrderItemRequestDto orderItemRequestDto : orderItemRequests){
            OrderItem orderItem = OrderItemMapper.fromOrderItemRequestDto(orderItemRequestDto);
            Long bookId = orderItemRequestDto.bookId();
            Book book = updatedBooks.stream()
                    .filter(b -> Objects.equals(b.getId(), bookId))
                    .findFirst()
                    .orElseThrow(() -> new BookNotFoundException(bookId));

            if(!validateQuantity(orderItemRequestDto.quantity(), book.getStock())) {
                throw new InsufficientStockException("Required quantity (" + orderItem.getQuantity() + ") is more than the available stock (" + book.getStock() + ")");
            }

            book.setStock(book.getStock() - orderItemRequestDto.quantity());

            orderItem.setBook(book);
            orderItem.setOrder(newOrder);
            orderItem.setTotalItemPrice(book.getPrice().multiply(BigDecimal.valueOf(orderItemRequestDto.quantity())));
            newOrderItems.add(orderItem);
        }
        orderItemRepo.saveAll(newOrderItems);
        bookRepo.saveAll(updatedBooks);
        return newOrderItems;
    }

    private List<Book> returnBookStocks(List<OrderItem> oldOrderItems, List<Book> oldBooks) {
        List<Book> updatedBooks = new ArrayList<>();
        for(OrderItem item : oldOrderItems){
            Long bookId = item.getBook().getId();
            Book book = oldBooks.stream().filter(b -> Objects.equals(b.getId(), bookId)).findFirst().orElseThrow(() -> new BookNotFoundException(bookId));
            book.setStock(book.getStock() + item.getQuantity());
            updatedBooks.add(book);
        }
        return updatedBooks;
    }

    @Override
    public void cancelOrderById(Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_ADMIN"));

        if(!Objects.equals(userDetails.getId(), order.getUser().getId()) && !isAdmin)  {
            throw new AccessDeniedException("You are not authorized to cancel this order.");
        }

        if(order.getStatus() != OrderStatus.PROCESSING && order.getStatus() != OrderStatus.CANCELLED && !isAdmin){
            throw new IllegalStateException("Cannot cancel an order after it is finished processing.");
        }

        order.setStatus(OrderStatus.CANCELLED);

        // update the inventory
        List<OrderItem> orderItems = new ArrayList<>(order.getOrderItems());
        List<Long> oldBookIds = orderItems.stream().mapToLong(oi -> oi.getBook().getId()).boxed().toList();
        List<Book> oldBooks = bookRepo.findAllById(oldBookIds);
        List<Book> updatedBooks = returnBookStocks(orderItems , oldBooks);
        bookRepo.saveAll(updatedBooks);
        orderItemRepo.deleteAll(orderItems);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        orderRepo.updateOrderStatus(orderId, status);
    }
}
