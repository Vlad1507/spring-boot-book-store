package com.store.bookstore.dto.user;

import com.store.bookstore.validation.match.FieldMatch;
import com.store.bookstore.validation.password.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@FieldMatch({"password", "repeatPassword"})
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;
    @Password
    @NotBlank
    private String password;
    @NotBlank
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String shippingAddress;

}
