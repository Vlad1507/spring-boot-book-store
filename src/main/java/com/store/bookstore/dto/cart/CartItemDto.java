package com.store.bookstore.dto.cart;

public record CartItemDto(
        Long bookId,
        int quantity) {
}
