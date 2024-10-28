package com.store.bookstore.services.impl;

import com.store.bookstore.dto.BookDto;
import com.store.bookstore.dto.CreateBookRequestDto;
import com.store.bookstore.exception.EntityNotFoundException;
import com.store.bookstore.mapper.BookMapper;
import com.store.bookstore.models.Book;
import com.store.bookstore.repository.BookRepository;
import com.store.bookstore.services.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't get a book by id: " + id)
                );
        return bookMapper.toDto(book);
    }
}
