package com.store.bookstore.services.book;

import com.store.bookstore.dto.book.BookDto;
import com.store.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.store.bookstore.dto.book.BookSearchParametersDto;
import com.store.bookstore.dto.book.CreateUpdateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateUpdateBookRequestDto requestDto);

    Page<BookDtoWithoutCategoryIds> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto update(Long id, CreateUpdateBookRequestDto bookRequestDto);

    void deleteById(Long id);

    List<BookDtoWithoutCategoryIds> searchBooks(BookSearchParametersDto params, Pageable pageable);

    Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long categoryId, Pageable pageable);
}
