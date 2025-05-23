package com.store.bookstore.services.order;

import com.store.bookstore.dto.order.OrderDto;
import com.store.bookstore.dto.order.OrderItemDto;
import com.store.bookstore.dto.order.OrderRequestDto;
import com.store.bookstore.exception.EntityNotFoundException;
import com.store.bookstore.exception.OrderProcessingException;
import com.store.bookstore.mapper.OrderItemMapper;
import com.store.bookstore.mapper.OrderMapper;
import com.store.bookstore.models.Book;
import com.store.bookstore.models.Order;
import com.store.bookstore.models.OrderItem;
import com.store.bookstore.models.ShoppingCart;
import com.store.bookstore.models.User;
import com.store.bookstore.repository.book.BookRepository;
import com.store.bookstore.repository.cart.ShoppingCartRepository;
import com.store.bookstore.repository.order.OrderItemRepository;
import com.store.bookstore.repository.order.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public OrderDto createOrder(User user, OrderRequestDto orderRequestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't get a shopping cart "
                                + "by user id: " + user.getId())
        );
        Set<OrderItem> orderItems = getOrderItemsFromShoppingCartOrThrowIfEmpty(shoppingCart);
        shoppingCart.clear();
        shoppingCartRepository.save(shoppingCart);
        Order order = new Order();
        order.setTotal(getTotalPrice(orderItems));
        order.setUser(user);
        order.setShippingAddress(orderRequestDto.shippingAddress());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setOrderItems(orderItems);
        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getAllOrders(User user, Pageable pageable) {
        return orderRepository.findAllByUserId(user.getId(), pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public Page<OrderItemDto> getAllItemsByOrderId(User user, Long orderId, Pageable pageable) {
        Order orderByUser = getOrderByUser(user, orderId, pageable);
        Set<OrderItem> orderItems = orderByUser.getOrderItems();
        Page<OrderItem> orderItemDtoPage = new PageImpl<>(orderItems.stream().toList());
        return orderItemDtoPage.map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto getOrderItemByOrderIdAndItemId(User user, Long orderId, Long itemId) {
        Set<OrderItem> orderItemByUserIdAndOrderId =
                getOrderByUser(user, orderId, Pageable.unpaged()).getOrderItems();
        OrderItem orderItemById = getOrderItemById(itemId);
        OrderItem orderItem = orderItemByUserIdAndOrderId.stream()
                .filter(item -> item.getId().equals(orderItemById.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't get a order item "
                                + "by id: " + itemId)
                );
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public OrderDto updateOrderStatus(User user, Long orderId, String status) {
        Order orderById = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't get a order "
                                + "by id: " + orderId)
                );
        orderById.setStatus(Order.Status.valueOf(status.toUpperCase()));
        return orderMapper.toDto(orderRepository.save(orderById));
    }

    private Set<OrderItem> getOrderItemsFromShoppingCartOrThrowIfEmpty(ShoppingCart shoppingCart) {
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new OrderProcessingException("There is no items in shopping cart."
                    + " Unable to create an order");
        }
        return shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                            Book book = bookRepository.findById(cartItem.getBook().getId())
                                    .orElseThrow(
                                            () -> new EntityNotFoundException("Can't get a book "
                                                    + "by id: " + cartItem.getBook().getId()));
                            OrderItem orderItem = new OrderItem();
                            orderItem.setBook(book);
                            orderItem.setPrice(book.getPrice());
                            orderItem.setQuantity(cartItem.getQuantity());
                            return orderItem;
                        }
                ).collect(Collectors.toSet());
    }

    private Order getOrderByUser(User user, Long orderId, Pageable pageable) {
        return orderRepository
                .findAllByUserId(user.getId(), pageable).stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't get a order "
                                + "by id: " + orderId)
                );
    }

    private OrderItem getOrderItemById(Long itemId) {
        return orderItemRepository.findById(itemId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't get a order item "
                                + "by id: " + itemId)
                );
    }

    private static BigDecimal getTotalPrice(Set<OrderItem> orderItems) {
        return BigDecimal.valueOf(orderItems.stream()
                .map(orderItem -> orderItem.getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity()))
                )
                .mapToInt(BigDecimal::intValue)
                .sum());
    }
}
