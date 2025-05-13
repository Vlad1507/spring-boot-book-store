package com.store.bookstore.dto.order;

import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(@NotNull String shippingAddress) {
}
