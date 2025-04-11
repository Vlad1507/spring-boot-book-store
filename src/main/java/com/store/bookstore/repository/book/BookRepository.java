package com.store.bookstore.repository.book;

import com.store.bookstore.models.Book;
import com.store.bookstore.models.Category;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Page<Book> findAllByCategoriesContains(Set<Category> categories, Pageable pageable);

    @EntityGraph(attributePaths = "categories")
    Optional<Book> findById(Long id);

    Page<Book> findAll(Pageable pageable);

    Page<Book> findAll(Specification<Book> bookSpecification, Pageable pageable);
}
