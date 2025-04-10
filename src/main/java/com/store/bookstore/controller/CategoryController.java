package com.store.bookstore.controller;

import com.store.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.store.bookstore.dto.category.CategoryDto;
import com.store.bookstore.dto.category.CategoryRequestDto;
import com.store.bookstore.services.book.BookService;
import com.store.bookstore.services.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Category management", description = "Endpoints for managing categories")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "Create category",
            description = "Create a new category if it does not exist already")
    public CategoryDto createCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        return categoryService.save(categoryRequestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/all")
    @Operation(summary = "Get all categories",
            description = "Return list of categories or empty list if no categories exist")
    public List<CategoryDto> getAll() {
        return categoryService.findAll();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    @Operation(summary = "Receive category by id",
            description = "Allows to get category by id")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update category",
            description = "Accept name and description of category by id")
    public CategoryDto updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequestDto categoryRequestDto
    ) {
        return categoryService.update(id, categoryRequestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category",
            description = "Delete category by id if exist")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/books")
    @Operation(summary = "Get books by category",
            description = "Allows to get list of books by category id")
    public Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            @PathVariable Long id, Pageable pageable) {
        return bookService.getBooksByCategoryId(id, pageable);
    }
}
