package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.cart.CartItemDto;
import com.store.bookstore.dto.cart.CartItemRequestDto;
import com.store.bookstore.dto.cart.CartItemWithBookTitleDto;
import com.store.bookstore.models.Book;
import com.store.bookstore.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "book", source = "bookId", qualifiedByName = "setBook")
    CartItem toModel(CartItemRequestDto cartItemRequestDto);

    @Named("setBook")
    default Book setBook(Long id) {
        Book book = new Book();
        book.setId(id);
        return book;
    }

    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "bookId", source = "book.id")
    CartItemWithBookTitleDto toDtoWithBookTitle(CartItem cartItem);

    @Mapping(target = "bookId", source = "book.id")
    CartItemDto toDto(CartItem cartItem);
}
