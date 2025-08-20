package com.store.bookstore.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.store.bookstore.dto.cart.CartItemRequestDto;
import com.store.bookstore.dto.cart.ShoppingCartDto;
import com.store.bookstore.dto.cart.UpdateCartItemRequestDto;
import com.store.bookstore.exception.EntityNotFoundException;
import com.store.bookstore.mapper.CartItemMapper;
import com.store.bookstore.mapper.ShoppingCartMapper;
import com.store.bookstore.models.Book;
import com.store.bookstore.models.CartItem;
import com.store.bookstore.models.ShoppingCart;
import com.store.bookstore.models.User;
import com.store.bookstore.repository.book.BookRepository;
import com.store.bookstore.repository.cart.CartItemRepository;
import com.store.bookstore.repository.cart.ShoppingCartRepository;
import com.store.bookstore.repository.user.UserRepository;
import com.store.bookstore.services.cart.ShoppingCartService;
import com.store.bookstore.util.BookUtil;
import com.store.bookstore.util.CartItemUtil;
import com.store.bookstore.util.ShoppingCartUtil;
import com.store.bookstore.util.UserUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class ShoppingCartServiceTest {
    @MockitoBean
    private final ShoppingCartMapper shoppingCartMapper;
    @MockitoBean
    private final ShoppingCartRepository shoppingCartRepository;
    @MockitoBean
    private final CartItemRepository cartItemRepository;
    @MockitoBean
    private final CartItemMapper cartItemMapper;
    @MockitoBean
    private final UserRepository userRepository;
    @MockitoBean
    private final BookRepository bookRepository;
    private final ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartServiceTest(ShoppingCartMapper shoppingCartMapper,
                                   ShoppingCartRepository shoppingCartRepository,
                                   CartItemRepository cartItemRepository,
                                   ShoppingCartService shoppingCartService,
                                   CartItemMapper cartItemMapper, UserRepository userRepository,
                                   BookRepository bookRepository) {
        this.shoppingCartMapper = shoppingCartMapper;
        this.shoppingCartRepository = shoppingCartRepository;
        this.cartItemRepository = cartItemRepository;
        this.shoppingCartService = shoppingCartService;
        this.cartItemMapper = cartItemMapper;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Test
    @DisplayName("Should return a shopping cart with items for the existing user")
    void getShoppingCartWithItems_GetShoppingCartByUserId_ShouldReturnCartWithListOfItems() {
        User user = UserUtil.getUserFromDb();
        ShoppingCart shoppingCart = ShoppingCartUtil.getShoppingCart();
        ShoppingCartDto expected = ShoppingCartUtil.getShoppingCartDto();
        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.getShoppingCartWithItems(user);

        Assertions.assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should throw an error while using an invalid user ID.")
    void getShoppingCartWithItems_GetShoppingCartByInvalidUserId_ShouldThrowError() {
        User user = new User();
        user.setId(99L);
        when(shoppingCartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        String expected = "Can't get shopping cart by user id: " + user.getId();

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCartWithItems(user));

        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should add items to the user shopping cart "
            + "and return dto of shopping cart with items.")
    void addItemToCart_AddItemToUserCart_ShouldReturnCartDtoWithItems() {
        ShoppingCart emptyShoppingCart = ShoppingCartUtil.getEmptyShoppingCart();
        ShoppingCart shoppingCart = ShoppingCartUtil.getShoppingCart();
        User user = UserUtil.getUserFromDb();
        Book book = BookUtil.getBook();
        CartItem cartItem = CartItemUtil.getCartItem();
        CartItemRequestDto cartItemRequestDto = CartItemUtil.getCartItemRequestDto();
        ShoppingCartDto shoppingCartDto = ShoppingCartUtil.getShoppingCartDto();

        when(bookRepository.existsById(book.getId())).thenReturn(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(emptyShoppingCart));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);
        when(cartItemMapper.toModel(cartItemRequestDto)).thenReturn(cartItem);
        when(shoppingCartRepository.save(emptyShoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(emptyShoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto actual = shoppingCartService.addItemToCart(user, cartItemRequestDto);
        ShoppingCartDto expected = shoppingCartMapper.toDto(emptyShoppingCart);
        assertEquals(expected, actual);
        assertNotNull(actual.cartItems());
    }

    @Test
    @DisplayName("Should throw an error due to an invalid user ID.")
    void addItemToCart_AddItemToCartWithInvalidUserId_ShouldThrowError() {
        User user = new User();
        user.setId(99L);
        Book book = BookUtil.getBook();
        CartItemRequestDto cartItemRequestDto = CartItemUtil.getCartItemRequestDto();
        String expected = "Can't get shopping cart by user id: " + user.getId();
        when(bookRepository.findById(cartItemRequestDto.bookId())).thenReturn(Optional.of(book));
        when(userRepository.findById(user.getId()))
                .thenThrow(new EntityNotFoundException(expected));

        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addItemToCart(user, cartItemRequestDto));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should throw an error due to an invalid book ID.")
    void addItemToCart_AddItemToCartWithInvalidBookId_ShouldThrowError() {
        Book book = new Book();
        book.setId(99L);
        User user = UserUtil.getUserFromDb();
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(book.getId(), 10);
        ShoppingCart emptyShoppingCart = ShoppingCartUtil.getEmptyShoppingCart();
        String expected = "Can't find a book by id: " + book.getId();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(emptyShoppingCart));
        when(bookRepository.findById(book.getId()))
                .thenThrow(new EntityNotFoundException(expected));

        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addItemToCart(user, cartItemRequestDto));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should return a shopping cart dto with updated quantity of the item.")
    void updateCartItemQuantity_ChangeQuantityExistedItem_ShouldReturnUpdatedItemQuantity() {
        ShoppingCart shoppingCart = ShoppingCartUtil.getShoppingCart();
        User user = UserUtil.getUserFromDb();
        CartItem cartItem = CartItemUtil.getCartItem();
        UpdateCartItemRequestDto updateCartItemRequestDto =
                CartItemUtil.getUpdateCartItemRequestDto();
        ShoppingCartDto expected = ShoppingCartUtil.getUpdatedShoppingCartDto();

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCart_Id(cartItem.getId(), shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService
                .updateCartItemQuantity(user, cartItem.getId(), updateCartItemRequestDto);
        assertNotNull(actual.cartItems());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should throw an error due to non existed item ID in the shopping cart")
    void updateCartItemQuantity_ChangeQuantityNotExistedItem_ShouldThrowError() {
        ShoppingCart shoppingCart = ShoppingCartUtil.getShoppingCart();
        User user = UserUtil.getUserFromDb();
        CartItem cartItem = new CartItem();
        cartItem.setId(99L);
        UpdateCartItemRequestDto updateCartItemRequestDto = new UpdateCartItemRequestDto(100);
        String expected = "Can't get cart item from database by id: " + cartItem.getId();

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCart_Id(cartItem.getId(), shoppingCart.getId()))
                .thenThrow(new EntityNotFoundException(expected));

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService
                        .updateCartItemQuantity(user, cartItem.getId(), updateCartItemRequestDto));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should delete an item by exists ID from a shopping cart.")
    void deleteItemFromCartById_DeleteItemByExistedId_ShouldRemoveItem() {
        ShoppingCart shoppingCart = ShoppingCartUtil.getShoppingCart();
        User user = UserUtil.getUserFromDb();
        CartItem cartItem = shoppingCart.getCartItems().stream().findFirst().get();

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCart_Id(cartItem.getId(), shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));

        shoppingCartService.deleteItemFromCartById(user, cartItem.getId());
        verify(cartItemRepository, times(1)).deleteById(cartItem.getId());
        assertFalse(shoppingCart.getCartItems().contains(cartItem));
    }

    @Test
    @DisplayName("Should throw an error due to invalid item ID.")
    void deleteItemFromCartById_DeleteItemByNotExistedItemId_ShouldThrowError() {
        ShoppingCart shoppingCart = ShoppingCartUtil.getShoppingCart();
        User user = UserUtil.getUserFromDb();
        CartItem cartItem = new CartItem();
        cartItem.setId(99L);
        String expected = "Can't get cart item from database by id: " + cartItem.getId();

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCart_Id(cartItem.getId(), shoppingCart.getId()))
                .thenThrow(new EntityNotFoundException(expected));

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.deleteItemFromCartById(user, cartItem.getId()));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should create a shopping cart for the user from the database")
    void createShoppingCartForUser_ShouldCreateShoppingCartForUser_ShouldReturnCart() {
        User user = UserUtil.getUserFromDb();
        ShoppingCart shoppingCart = ShoppingCartUtil.getEmptyShoppingCart();
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        shoppingCartService.createShoppingCartForUser(user);

        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

}
