package com.store.bookstore.services.category;

import com.store.bookstore.dto.category.CategoryDto;
import com.store.bookstore.dto.category.CategoryRequestDto;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll();

    CategoryDto getById(Long id);

    CategoryDto save(CategoryRequestDto categoryRequestDto);

    CategoryDto update(Long id, CategoryRequestDto categoryRequestDto);

    void deleteById(Long id);
}
