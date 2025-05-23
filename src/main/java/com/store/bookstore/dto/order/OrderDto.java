package com.store.bookstore.dto.order;

import java.math.BigDecimal;
import java.util.Set;

public record OrderDto(
        Long id,
        Long userId,
        Set<OrderItemDto> orderItemsDtos,
        String orderDate,
        BigDecimal total,
        String status
) {
}
