package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.cart.CartDto;
import com.store.bookstore.models.ShoppingCart;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {

    Page<CartDto> toDto(ShoppingCart user);
}
