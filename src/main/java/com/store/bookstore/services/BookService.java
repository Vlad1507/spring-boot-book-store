package com.store.bookstore.services;

import com.store.bookstore.dto.BookDto;
import com.store.bookstore.dto.BookSearchParametersDto;
import com.store.bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto update(Long id, CreateBookRequestDto bookRequestDto);

    void deleteById(Long id);

    List<BookDto> searchBooks(BookSearchParametersDto params);
}
