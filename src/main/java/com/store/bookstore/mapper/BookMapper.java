package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.book.BookDto;
import com.store.bookstore.dto.book.CreateBookRequestDto;
import com.store.bookstore.models.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(CreateBookRequestDto bookRequestDto, @MappingTarget Book book);
}
