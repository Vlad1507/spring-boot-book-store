package com.store.bookstore.repository;

import com.store.bookstore.models.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
