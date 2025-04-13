package com.store.bookstore.services.cart;

import com.store.bookstore.dto.cart.CartItemDto;
import com.store.bookstore.dto.cart.CartItemRequestDto;
import com.store.bookstore.dto.cart.ShoppingCartDto;
import com.store.bookstore.dto.cart.UpdateCartItemRequestDto;
import com.store.bookstore.models.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(User user, Pageable pageable);

    CartItemDto addItemToCart(User user, @Valid CartItemRequestDto cartItemRequestDto);

    CartItemDto updateCartItemQuantity(User user, Long itemId,
                                       UpdateCartItemRequestDto updateCartItemRequestDto);

    void deleteItemFromCartById(User user, Long itemId);
}
