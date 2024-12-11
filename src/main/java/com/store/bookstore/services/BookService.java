package com.store.bookstore.services;

import com.store.bookstore.dto.BookDto;
import com.store.bookstore.dto.BookSearchParametersDto;
import com.store.bookstore.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto update(Long id, CreateBookRequestDto bookRequestDto);

    void deleteById(Long id);

    List<BookDto> searchBooks(BookSearchParametersDto params);
}
