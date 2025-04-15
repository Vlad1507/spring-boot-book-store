package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.cart.CartItemWithBookTitleDto;
import com.store.bookstore.dto.cart.ShoppingCartDto;
import com.store.bookstore.models.CartItem;
import com.store.bookstore.models.ShoppingCart;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(target = "cartItems", source = "cartItems", qualifiedByName = "setCartItems")
    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Named("setCartItems")
    default Set<CartItemWithBookTitleDto> setCartItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> new CartItemWithBookTitleDto(
                        cartItem.getId(),
                        cartItem.getBook().getId(),
                        cartItem.getBook().getTitle(),
                        cartItem.getQuantity())
                )
                .collect(Collectors.toSet());
    }
}
