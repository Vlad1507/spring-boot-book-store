package com.store.bookstore.services.user;

import com.store.bookstore.dto.user.UserDto;
import com.store.bookstore.dto.user.UserRegistrationRequestDto;
import com.store.bookstore.exception.RegistrationException;
import com.store.bookstore.mapper.UserMapper;
import com.store.bookstore.models.Role;
import com.store.bookstore.models.User;
import com.store.bookstore.repository.role.RoleRepository;
import com.store.bookstore.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Long USER_ID = 2L;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(UserRegistrationRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RegistrationException("This email was already registered "
                    + request.getEmail());
        }
        User user = userMapper.toModel(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(getRole()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    private Role getRole() {
        return roleRepository.findById(USER_ID)
                .orElseThrow(() -> new RuntimeException("Can't find role by id: "
                        + USER_ID));
    }
}
