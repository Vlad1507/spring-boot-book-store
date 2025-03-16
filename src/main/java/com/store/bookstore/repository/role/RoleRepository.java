package com.store.bookstore.repository.role;

import com.store.bookstore.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
