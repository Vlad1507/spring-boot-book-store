package com.store.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.store.bookstore.models.Book;
import com.store.bookstore.models.Category;
import com.store.bookstore.repository.book.BookRepository;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    private final BookRepository bookRepository;

    @Autowired
    public BookRepositoryTest(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    @DisplayName("Search for all books by category ID with an existing category ID. "
            + "Should return concrete category")
    @Sql(scripts = {
            "classpath:database/categories/insert_2_categories.sql",
            "classpath:database/books/insert_2_books.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/delete_all_books.sql",
            "classpath:database/categories/delete_all_categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoriesContains_BooksByCategoryExists_ShouldReturnBooks() {
        Book book = new Book();
        book.setId(1L);

        Category category = new Category(1L);
        category.setName("adventure");
        book.setCategories(Set.of(category));

        Page<Book> bookPage = bookRepository
                .findAllByCategoriesContains(Set.of(category), Pageable.unpaged());
        List<Book> bookList = bookPage.toList();

        assertEquals(1, bookList.size());
        Book actual = bookList.stream().findFirst().get();
        assertEquals(book.getCategories().stream().findFirst().get().getName(),
                actual.getCategories().stream().findFirst().get().getName());
    }

    @Test
    @DisplayName("Search for all books by category ID with a non-existent category ID."
            + " Should return empty list")
    void findAllByCategoriesContains_BooksByCategoryNonExistent_ShouldReturnError() {
        Book book = new Book();
        book.setId(1L);
        Category category = new Category(999L);
        book.setCategories(Set.of(category));

        List<Book> actual = bookRepository
                .findAllByCategoriesContains(Set.of(category), Pageable.unpaged())
                .getContent();
        assertEquals(Collections.emptyList(), actual);
    }

    @Test
    @DisplayName("Search for book by ID with an existing ID. Should return book")
    @Sql(scripts = {
            "classpath:database/categories/insert_2_categories.sql",
            "classpath:database/books/insert_2_books.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/delete_all_books.sql",
            "classpath:database/categories/delete_all_categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_BookExistsById_ReturnBook() {
        Book book = new Book();
        book.setId(1L);

        Book byId = bookRepository.findById(book.getId()).orElseThrow();

        assertEquals(1, byId.getId());
    }

    @Test
    @DisplayName("Search for book by ID with an non-existent ID. Should return empty optional")
    @Sql(scripts = {
            "classpath:database/categories/insert_2_categories.sql",
            "classpath:database/books/insert_2_books.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books/delete_all_books.sql",
            "classpath:database/categories/delete_all_categories.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_BookNotExistsById_ReturnEmpty() {
        Book book = new Book();
        book.setId(999L);

        assertThrows(NoSuchElementException.class,
                () -> bookRepository.findById(book.getId()).orElseThrow());
    }
}
