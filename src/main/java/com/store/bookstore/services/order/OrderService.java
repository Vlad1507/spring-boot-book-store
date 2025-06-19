package com.store.bookstore.services.order;

import com.store.bookstore.dto.order.OrderDto;
import com.store.bookstore.dto.order.OrderItemDto;
import com.store.bookstore.dto.order.OrderRequestDto;
import com.store.bookstore.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(User user, OrderRequestDto orderRequestDto);

    Page<OrderDto> getAllOrders(User user, Pageable pageable);

    Page<OrderItemDto> getAllItemsByOrderId(User user, Long orderId, Pageable pageable);

    OrderItemDto getOrderItemByOrderIdAndItemId(User user, Long orderId, Long itemId);

    OrderDto updateOrderStatus(User user, Long orderId, String status);
}
