package com.store.bookstore.repository.cart;

import com.store.bookstore.models.CartItem;
import com.store.bookstore.models.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndShoppingCart(Long id, ShoppingCart shoppingCart);
}
