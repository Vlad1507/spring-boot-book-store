package com.store.bookstore.controller;

import com.store.bookstore.dto.book.BookDto;
import com.store.bookstore.dto.book.BookSearchParametersDto;
import com.store.bookstore.dto.book.CreateBookRequestDto;
import com.store.bookstore.services.book.BookService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public BookDto update(@PathVariable Long id, @RequestBody CreateBookRequestDto bookRequestDto) {
        return bookService.update(id, bookRequestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/search")
    public List<BookDto> searchBooks(BookSearchParametersDto params) {
        return bookService.searchBooks(params);
    }
}
