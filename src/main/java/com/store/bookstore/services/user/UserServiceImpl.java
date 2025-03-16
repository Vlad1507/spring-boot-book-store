package com.store.bookstore.services.user;

import com.store.bookstore.dto.user.UserDto;
import com.store.bookstore.dto.user.UserRegistrationRequestDto;
import com.store.bookstore.exception.RegistrationException;
import com.store.bookstore.mapper.UserMapper;
import com.store.bookstore.models.User;
import com.store.bookstore.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(String email, UserRegistrationRequestDto request) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RegistrationException("This email was already registered");
        }
        User user = userMapper.toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
