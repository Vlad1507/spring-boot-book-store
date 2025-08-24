package com.store.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.store.bookstore.models.ShoppingCart;
import com.store.bookstore.repository.cart.ShoppingCartRepository;
import com.store.bookstore.util.ShoppingCartUtil;
import com.store.bookstore.util.UserUtil;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {
        "classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
        "classpath:database/users/delete_user.sql",
        "classpath:database/books/delete_all_books.sql",
        "classpath:database/categories/delete_all_categories.sql",
        "classpath:database/categories/insert_2_categories.sql",
        "classpath:database/users/insert_user.sql",
        "classpath:database/books/insert_2_books.sql",
        "classpath:database/shoppingCarts/insert_shopping_cart_wih_2_cart_items.sql"
},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {
        "classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
        "classpath:database/books/delete_all_books.sql",
        "classpath:database/categories/delete_all_categories.sql",
        "classpath:database/users/delete_user.sql"
},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {

    private final ShoppingCartRepository shoppingCartRepository;

    @Autowired
    public ShoppingCartRepositoryTest(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @Test
    @DisplayName("Search for shopping cart by user ID with an existing user. Should return book")
    void findByUserId_ValidUserId_ShouldReturnShoppingCart() {
        Long userId = UserUtil.getUserFromDb().getId();
        ShoppingCart expected = ShoppingCartUtil.getShoppingCart();

        Optional<ShoppingCart> shoppingCartByUserId = shoppingCartRepository.findByUserId(userId);
        assertTrue(shoppingCartByUserId.isPresent());
        
        ShoppingCart actual = shoppingCartByUserId.get();
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser().getId(), actual.getUser().getId());
        assertEquals(expected.isDeleted(), actual.isDeleted());
    }

    @Test
    @DisplayName("Search for book by ID with an non-existent ID. Should return empty optional")
    void findByUserId_InvalidUserId_ShouldThrowError() {
        Long userId = 99L;

        assertThrows(NoSuchElementException.class,
                () -> shoppingCartRepository.findByUserId(userId).orElseThrow());
    }
}
