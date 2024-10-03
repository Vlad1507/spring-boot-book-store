package com.store.bookstore.services;

import com.store.bookstore.models.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
