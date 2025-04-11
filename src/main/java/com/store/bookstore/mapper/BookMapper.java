package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.book.BookDto;
import com.store.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.store.bookstore.dto.book.CreateUpdateBookRequestDto;
import com.store.bookstore.models.Book;
import com.store.bookstore.models.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    Book toModel(CreateUpdateBookRequestDto requestDto);

    @Mapping(target = "categories", ignore = true)
    void updateBookFromDto(CreateUpdateBookRequestDto bookRequestDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        Set<Long> categoryIds = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoryIds(categoryIds);
    }

    @AfterMapping
    default void setCategories(
            CreateUpdateBookRequestDto updateBookRequestDto,
            @MappingTarget Book book
    ) {
        Set<Category> categories = updateBookRequestDto.getCategoryIds().stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        return null;
    }
}
