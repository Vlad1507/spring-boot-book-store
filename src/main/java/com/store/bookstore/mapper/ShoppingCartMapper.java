package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.cart.ShoppingCartDto;
import com.store.bookstore.models.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "cartItems", source = "cartItems")
    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
