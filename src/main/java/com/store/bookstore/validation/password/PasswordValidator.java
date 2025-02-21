package com.store.bookstore.validation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])"
                    + "(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,25}$";

    @Override
    public boolean isValid(String password,
                           ConstraintValidatorContext constraintValidatorContext) {
        return password != null && Pattern.compile(PASSWORD_PATTERN, Pattern.CASE_INSENSITIVE)
                .matcher(password)
                .matches();
    }
}
