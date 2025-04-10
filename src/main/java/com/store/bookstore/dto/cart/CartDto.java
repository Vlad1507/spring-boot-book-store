package com.store.bookstore.dto.cart;

import java.util.Set;

public record CartDto(
        Long cartId,
        Long userId,
        Set<Long> cartItemsIds
) {
}
