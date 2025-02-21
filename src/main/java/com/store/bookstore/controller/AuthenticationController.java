package com.store.bookstore.controller;

import com.store.bookstore.dto.user.UserDto;
import com.store.bookstore.dto.user.UserRegistrationRequestDto;
import com.store.bookstore.exception.RegistrationException;
import com.store.bookstore.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/registration")
    public UserDto register(@RequestBody UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }
}
