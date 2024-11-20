package com.store.bookstore.repository;

public interface SpecificationProviderManager<E> {
    SpecificationProvider<E> getSpecificationProvider(String key);
}
