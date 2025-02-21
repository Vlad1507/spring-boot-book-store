package com.store.bookstore.mapper;

import com.store.bookstore.config.MapperConfig;
import com.store.bookstore.dto.user.UserDto;
import com.store.bookstore.dto.user.UserRegistrationRequestDto;
import com.store.bookstore.models.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(UserRegistrationRequestDto userRequestDto);
}
