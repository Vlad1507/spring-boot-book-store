package com.store.bookstore.dto.cart;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(@Positive int quantity) {
}
