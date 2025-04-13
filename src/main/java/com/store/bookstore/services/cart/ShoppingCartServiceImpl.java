package com.store.bookstore.services.cart;

import com.store.bookstore.dto.cart.CartItemDto;
import com.store.bookstore.dto.cart.CartItemRequestDto;
import com.store.bookstore.dto.cart.CartItemWithBookTitleDto;
import com.store.bookstore.dto.cart.ShoppingCartDto;
import com.store.bookstore.dto.cart.UpdateCartItemRequestDto;
import com.store.bookstore.exception.EntityNotFoundException;
import com.store.bookstore.mapper.CartItemMapper;
import com.store.bookstore.mapper.ShoppingCartMapper;
import com.store.bookstore.models.CartItem;
import com.store.bookstore.models.ShoppingCart;
import com.store.bookstore.models.User;
import com.store.bookstore.repository.cart.CartItemRepository;
import com.store.bookstore.repository.cart.ShoppingCartRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getShoppingCart(User user, Pageable pageable) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("Can't get shopping cart by user: "
                        + user.getEmail())
        );
        Page<CartItem> cartItems = cartItemRepository.getByShoppingCart(shoppingCart, pageable);
        Set<CartItem> setItems = new HashSet<>(cartItems.getContent());
        Set<CartItemWithBookTitleDto> itemsDto = setItems.stream()
                .map(cartItemMapper::toDtoWithBookTitle)
                .collect(Collectors.toSet());

        ShoppingCartDto cartDto = shoppingCartMapper.toDto(shoppingCart);
        cartDto.cartItems().addAll(itemsDto);
        return cartDto;
    }

    @Override
    public CartItemDto addItemToCart(User user, CartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCartByUser = shoppingCartRepository.findByUser(user)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't get shopping cart by user id: " + user.getId())
                );
        CartItem cartItem = cartItemMapper.toModel(cartItemRequestDto);
        cartItem.setShoppingCart(shoppingCartByUser);
        cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public CartItemDto updateCartItemQuantity(
            User user, Long itemId,
            UpdateCartItemRequestDto updateCartItemRequestDto) {
        ShoppingCart shoppingCartByUser = shoppingCartRepository.findByUser(user)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't get shopping cart by user id: " + user.getId())
                );
        CartItem cartItemFromDb = shoppingCartByUser.getCartItems()
                .stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't get cart item from database by id: " + itemId
                        )
                );
        cartItemFromDb.setQuantity(updateCartItemRequestDto.quantity());
        cartItemRepository.save(cartItemFromDb);
        return cartItemMapper.toDto(cartItemFromDb);
    }

    @Override
    public void deleteItemFromCartById(User user, Long itemId) {
        ShoppingCart shoppingCartByUser = shoppingCartRepository.findByUser(user)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't get shopping cart by user id: " + user.getId())
                );
        CartItem cartItemFromDb = shoppingCartByUser.getCartItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't get cart item by id: " + itemId)
                );
        cartItemRepository.delete(cartItemFromDb);
    }
}
