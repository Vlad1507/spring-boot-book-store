package com.store.bookstore.dto.cart;

public record CartItemWithBookTitleDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity) {
}
