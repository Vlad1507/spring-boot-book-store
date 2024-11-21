package com.store.bookstore.repository.book;

import com.store.bookstore.dto.BookSearchParametersDto;
import com.store.bookstore.models.Book;
import com.store.bookstore.repository.SpecificationBuilder;
import com.store.bookstore.repository.SpecificationProviderManager;
import com.store.bookstore.repository.book.specification.AuthorSpecificationProvider;
import com.store.bookstore.repository.book.specification.IsbnSpecificationProvider;
import com.store.bookstore.repository.book.specification.TitleSpecificationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto bookSearchParametersDto) {
        Specification<Book> specification = Specification.where(null);
        if (bookSearchParametersDto.author() != null) {
            specification = bookSpecificationProviderManager
                    .getSpecificationProvider(AuthorSpecificationProvider.AUTHOR)
                    .getSpecification(bookSearchParametersDto.author());
        }
        if (bookSearchParametersDto.title() != null) {
            specification = bookSpecificationProviderManager
                    .getSpecificationProvider(TitleSpecificationProvider.TITLE)
                    .getSpecification(bookSearchParametersDto.title());
        }
        if (bookSearchParametersDto.isbn() != null) {
            specification = bookSpecificationProviderManager
                    .getSpecificationProvider(IsbnSpecificationProvider.ISBN)
                    .getSpecification(bookSearchParametersDto.isbn());
        }
        return specification;
    }
}
