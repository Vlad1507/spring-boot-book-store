package com.store.bookstore.util;

import com.store.bookstore.dto.cart.CartItemRequestDto;
import com.store.bookstore.dto.cart.UpdateCartItemRequestDto;
import com.store.bookstore.models.CartItem;

public class CartItemUtil {

    public static CartItem getCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(ShoppingCartUtil.getShoppingCart());
        cartItem.setBook(BookUtil.getBook());
        cartItem.setQuantity(10);
        return cartItem;
    }

    public static CartItemRequestDto getCartItemRequestDto() {
        return new CartItemRequestDto(1L, 10);
    }

    public static UpdateCartItemRequestDto getUpdateCartItemRequestDto() {
        return new UpdateCartItemRequestDto(15);
    }
}
