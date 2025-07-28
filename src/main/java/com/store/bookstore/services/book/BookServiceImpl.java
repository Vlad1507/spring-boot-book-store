package com.store.bookstore.services.book;

import com.store.bookstore.dto.book.BookDto;
import com.store.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.store.bookstore.dto.book.BookSearchParametersDto;
import com.store.bookstore.dto.book.CreateUpdateBookRequestDto;
import com.store.bookstore.exception.EntityNotFoundException;
import com.store.bookstore.mapper.BookMapper;
import com.store.bookstore.models.Book;
import com.store.bookstore.models.Category;
import com.store.bookstore.repository.SpecificationBuilder;
import com.store.bookstore.repository.book.BookRepository;
import com.store.bookstore.repository.category.CategoryRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final SpecificationBuilder<Book> specificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateUpdateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toDtoWithoutCategories);
    }

    @Override
    public BookDto findById(Long id) {
        Book book = getBookById(id);
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto update(Long id, CreateUpdateBookRequestDto bookRequestDto) {
        Book book = getBookById(id);
        bookMapper.updateBookFromDto(bookRequestDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Can't find book by id: " + id);
        }
    }

    @Override
    public List<BookDtoWithoutCategoryIds> searchBooks(
            BookSearchParametersDto params,
            Pageable pageable
    ) {
        Specification<Book> bookSpecification = specificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            Long categoryId, Pageable pageable
    ) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Can't find category by id: " + categoryId);
        }
        Page<Book> books = bookRepository
                .findAllByCategoriesContains(Set.of(new Category(categoryId)), pageable);
        return books.map(bookMapper::toDtoWithoutCategories);
    }

    private Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't get a book by id: " + id)
                );
    }
}
