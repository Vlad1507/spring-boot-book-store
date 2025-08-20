package com.store.bookstore.util;

import com.store.bookstore.dto.cart.CartItemWithBookTitleDto;
import com.store.bookstore.dto.cart.ShoppingCartDto;
import com.store.bookstore.models.CartItem;
import com.store.bookstore.models.ShoppingCart;
import java.util.HashSet;
import java.util.Set;

public class ShoppingCartUtil {

    public static ShoppingCart getShoppingCart() {
        final ShoppingCart shoppingCart = new ShoppingCart();
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(BookUtil.getBook());
        cartItem.setQuantity(10);
        shoppingCart.setId(1L);
        shoppingCart.setUser(UserUtil.getUserFromDb());
        shoppingCart.setCartItems(new HashSet<>(Set.of(cartItem)));
        shoppingCart.setDeleted(false);
        cartItem.setShoppingCart(shoppingCart);
        return shoppingCart;
    }

    public static ShoppingCart getEmptyShoppingCart() {
        final ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(UserUtil.getUserFromDb());
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCart.setDeleted(false);
        return shoppingCart;
    }

    public static ShoppingCartDto getShoppingCartDto() {
        return new ShoppingCartDto(
                1L,
                1L,
                Set.of(getCartItemWithBookTitleDto())

        );
    }

    public static ShoppingCartDto getShoppingCartWithTwoItemsDto() {
        return new ShoppingCartDto(
                1L,
                1L,
                Set.of(getCartItemWithBookTitleDto(), getCartItemWithSecondBookTitleDto())

        );
    }

    public static CartItemWithBookTitleDto getCartItemWithBookTitleDto() {
        return new CartItemWithBookTitleDto(
                1L,
                1L,
                "White Fang",
                10
        );
    }

    public static CartItemWithBookTitleDto getCartItemWithSecondBookTitleDto() {
        return new CartItemWithBookTitleDto(
                2L,
                2L,
                "The Intelligent Investor",
                15
        );
    }

    public static CartItemWithBookTitleDto getUpdatedCartItemWithBookTitleDto() {
        return new CartItemWithBookTitleDto(
                1L,
                1L,
                "White Fang",
                15
        );
    }

    public static ShoppingCartDto getUpdatedShoppingCartDto() {
        return new ShoppingCartDto(
                1L,
                1L,
                Set.of(getUpdatedCartItemWithBookTitleDto())
        );
    }
}
