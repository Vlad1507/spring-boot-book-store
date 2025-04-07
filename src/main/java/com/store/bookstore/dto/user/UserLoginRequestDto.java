package com.store.bookstore.dto.user;

import com.store.bookstore.validation.password.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    @NotBlank
    private String email;
    @Password
    @NotBlank
    private String password;
}
