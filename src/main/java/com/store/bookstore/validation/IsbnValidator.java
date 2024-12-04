package com.store.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    private static final String ISBN_PATTERN = "(\\d{3})(\\d)(\\d{5})(\\d{3})(\\d)";

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext constraintValidatorContext) {
        Matcher matcher = Pattern.compile(ISBN_PATTERN).matcher(isbn);
        String formattedIsbn = matcher.replaceFirst("$1-$2-$3-$4-$5");
        return isbn.equals(formattedIsbn);
    }
}
