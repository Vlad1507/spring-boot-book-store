package com.store.bookstore.dto.book;

import java.math.BigDecimal;
import java.util.Set;

import com.store.bookstore.models.Category;
import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
    private Set<Long> categoryIds;
}
