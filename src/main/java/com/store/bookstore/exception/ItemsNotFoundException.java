package com.store.bookstore.exception;

public class ItemsNotFoundException extends RuntimeException {
    public ItemsNotFoundException(String message) {
        super(message);
    }
}
