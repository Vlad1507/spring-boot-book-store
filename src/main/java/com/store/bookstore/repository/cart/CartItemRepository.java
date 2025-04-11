package com.store.bookstore.repository.cart;

import com.store.bookstore.models.CartItem;
import com.store.bookstore.models.ShoppingCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = "book.title")
    Page<CartItem> getByShoppingCart(ShoppingCart shoppingCart, Pageable pageable);
}
