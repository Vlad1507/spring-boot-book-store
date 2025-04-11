package com.store.bookstore.repository.cart;

import com.store.bookstore.models.ShoppingCart;
import com.store.bookstore.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}
