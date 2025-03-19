package com.store.bookstore.repository.role;

import com.store.bookstore.models.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query(value = "SELECT * FROM roles WHERE role = :roleName", nativeQuery = true)
    Optional<Role> findByName(Role.RoleName roleName);
}
