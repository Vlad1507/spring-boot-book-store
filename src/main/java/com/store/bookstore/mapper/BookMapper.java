package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.BookDto;
import com.store.bookstore.dto.CreateBookRequestDto;
import com.store.bookstore.models.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.jpa.repository.Modifying;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    @Modifying
    void updateBookFromDto(CreateBookRequestDto bookRequestDto, @MappingTarget Book book);
}
