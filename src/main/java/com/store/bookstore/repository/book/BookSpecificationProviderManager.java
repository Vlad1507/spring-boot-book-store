package com.store.bookstore.repository.book;

import com.store.bookstore.models.Book;
import com.store.bookstore.repository.SpecificationProvider;
import com.store.bookstore.repository.SpecificationProviderManager;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(provider -> provider.getKey().equals(key))
                .findFirst()
                .orElseThrow(
                        () -> new NoSuchElementException("Can't get "
                                + "specification provider for key: " + key)
                );
    }
}
