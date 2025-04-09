package com.store.bookstore.controller;

import com.store.bookstore.dto.user.UserDto;
import com.store.bookstore.dto.user.UserLoginRequestDto;
import com.store.bookstore.dto.user.UserLoginResponseDto;
import com.store.bookstore.dto.user.UserRegistrationRequestDto;
import com.store.bookstore.exception.RegistrationException;
import com.store.bookstore.security.AuthenticationService;
import com.store.bookstore.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management", description = "Endpoints for managing users")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "User registration",
            description = "Register a new user if it does not exist already")
    public UserDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "User login",
            description = "Authorizes the user who is logged in"
                    + " and provides them with a session key")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
