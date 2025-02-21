package com.store.bookstore.services.user;

import com.store.bookstore.dto.user.UserDto;
import com.store.bookstore.dto.user.UserRegistrationRequestDto;

public interface UserService {
    UserDto register(UserRegistrationRequestDto request);

}
