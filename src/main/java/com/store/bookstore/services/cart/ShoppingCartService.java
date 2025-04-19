package com.store.bookstore.services.cart;

import com.store.bookstore.dto.cart.CartItemRequestDto;
import com.store.bookstore.dto.cart.ShoppingCartDto;
import com.store.bookstore.dto.cart.UpdateCartItemRequestDto;
import com.store.bookstore.models.User;

public interface ShoppingCartService {

    ShoppingCartDto addItemToCart(User user, CartItemRequestDto cartItemRequestDto);

    ShoppingCartDto updateCartItemQuantity(User user, Long itemId,
                                       UpdateCartItemRequestDto updateCartItemRequestDto);

    void deleteItemFromCartById(User user, Long itemId);

    void createShoppingCartForUser(User user);

    ShoppingCartDto getShoppingCartWithItems(User user);
}
