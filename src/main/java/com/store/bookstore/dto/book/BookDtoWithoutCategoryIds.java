package com.store.bookstore.dto.book;

import com.store.bookstore.models.Category;
import java.math.BigDecimal;
import java.util.Set;

public record BookDtoWithoutCategoryIds(
        Long id,
        String title,
        String author,
        String isbn,
        BigDecimal price,
        String description,
        String coverImage
) {
}
