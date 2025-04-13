package com.store.bookstore.dto.cart;

import java.util.Set;

public record ShoppingCartDto(
        Long cartId,
        Long userId,
        Set<CartItemWithBookTitleDto> cartItems
) {
}
