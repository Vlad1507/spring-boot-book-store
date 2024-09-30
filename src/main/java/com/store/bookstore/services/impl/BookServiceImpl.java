package com.store.bookstore.services.impl;

import com.store.bookstore.models.Book;
import com.store.bookstore.repository.BookRepository;
import com.store.bookstore.services.BookService;
import java.util.List;

public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
