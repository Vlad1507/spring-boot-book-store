package com.store.bookstore.controller;

import com.store.bookstore.dto.order.OrderDto;
import com.store.bookstore.dto.order.OrderItemDto;
import com.store.bookstore.dto.order.OrderRequestDto;
import com.store.bookstore.models.User;
import com.store.bookstore.services.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "", description = "")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto addOrder(@RequestBody @Valid OrderRequestDto orderRequestDto,
                             Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.createOrder(user, orderRequestDto);
    }

    @Operation
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public Page<OrderDto> getOrderHistory(Pageable pageable, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrders(user, pageable);
    }

    @Operation
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items")
    public Page<OrderItemDto> getOrderItemsByOrderId(
            @PathVariable Long orderId,
            Pageable pageable,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllItemsByOrderId(user, orderId, pageable);
    }

    @Operation
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItemByItemId(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItemByOrderIdAndItemId(user, orderId, itemId);
    }

    @Operation
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto updateOrderStatus(@PathVariable Long orderId,
                                      @RequestParam String status,
                                      Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.updateOrderStatus(user, orderId, status);
    }
}
