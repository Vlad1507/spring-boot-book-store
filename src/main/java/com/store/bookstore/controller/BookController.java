package com.store.bookstore.controller;

import com.store.bookstore.dto.book.BookDto;
import com.store.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.store.bookstore.dto.book.BookSearchParametersDto;
import com.store.bookstore.dto.book.CreateUpdateBookRequestDto;
import com.store.bookstore.dto.exception.ExceptionDto;
import com.store.bookstore.dto.exception.ValidationExceptionDto;
import com.store.bookstore.services.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

@Tag(name = "Book management", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Receive all books",
            description = "Allows to return all books in page presentation")
    public Page<BookDtoWithoutCategoryIds> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @ApiResponse(responseCode = "404",
            description = "Couldn't find the book",
            content = @Content(schema = @Schema(
                    implementation = ExceptionDto.class
            ))
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    @Operation(summary = "Return book by id",
            description = "Return the book by the ID entered in the endpoint")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @ApiResponse(responseCode = "400",
            description = "Validation failed",
            content = @Content(schema = @Schema(
                    implementation = ValidationExceptionDto.class
            ))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "Create book",
            description = "Allows to create book by required parameters")
    public BookDto createBook(@RequestBody @Valid CreateUpdateBookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update book",
            description = "Allows to update book's parameters by ID entered in the endpoint")
    @ResponseStatus(HttpStatus.OK)
    public BookDto update(@PathVariable Long id,
                          @RequestBody @Valid CreateUpdateBookRequestDto bookRequestDto) {
        return bookService.update(id, bookRequestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete book", description = "Delete book by id if exist")
    public void deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/search")
    @Operation(summary = "Search book",
            description = "Allows to find book by some parameters(title, author or isbn)")
    public List<BookDtoWithoutCategoryIds> searchBooks(
            @RequestBody BookSearchParametersDto params, Pageable pageable
    ) {
        return bookService.searchBooks(params, pageable);
    }
}
