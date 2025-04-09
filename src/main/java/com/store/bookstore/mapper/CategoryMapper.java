package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.category.CategoryDto;
import com.store.bookstore.dto.category.CategoryRequestDto;
import com.store.bookstore.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CategoryRequestDto categoryRequestDto);

    void updateCategoryFromDB(CategoryRequestDto categoryRequestDto, @MappingTarget Category category);
}
