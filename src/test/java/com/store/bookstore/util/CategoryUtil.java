package com.store.bookstore.util;

import com.store.bookstore.dto.category.CategoryDto;
import com.store.bookstore.dto.category.CategoryRequestDto;
import com.store.bookstore.models.Category;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CategoryUtil {

    public static Category getCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("adventure");
        category.setDescription("long exciting journeys books");
        return category;
    }

    public static CategoryDto getCategoryDto() {
        return new CategoryDto(
               1L,
                "adventure",
                "long exciting journeys books"
        );
    }

    public static CategoryRequestDto getCategoryRequestDto() {
        return new CategoryRequestDto(
                "adventure",
                "long exciting journeys books"
        );
    }

    public static CategoryRequestDto getInvalidCategoryRequestDto() {
        return new CategoryRequestDto(null, "description");
    }

    public static Pageable getPageable() {
        return PageRequest.of(0,10, Sort.by("name"));
    }

    public static CategoryDto getThirdCategoryDto() {
        return new CategoryDto(
                3L,
                "detective",
                "novels with mysterious plots");
    }

    public static CategoryRequestDto getThirdCategoryRequestDto() {
        return new CategoryRequestDto(
                "detective",
                "novels with mysterious plots");
    }

    public static List<CategoryDto> getCategoryDtoList() {
        CategoryDto first = new CategoryDto(
                1L,
                "adventure",
                "long exciting journeys books"
        );
        CategoryDto second = new CategoryDto(
                2L,
                "investment",
                "books about money management"
        );
        return List.of(first, second);
    }
}
