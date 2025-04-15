package com.store.bookstore.services.cart;

import com.store.bookstore.dto.cart.CartItemDto;
import com.store.bookstore.dto.cart.CartItemRequestDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

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
        CartItem cartItemFromDb = cartItemRepository
                .findByIdAndShoppingCart(itemId, shoppingCartByUser)
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
        CartItem cartItemFromDb = cartItemRepository
                .findByIdAndShoppingCart(itemId, shoppingCartByUser)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't get cart item from database by id: " + itemId
                        )
                );
        cartItemRepository.delete(cartItemFromDb);
    }

    @Override
    public void createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto getShoppingCartWithItems(User user) {
        ShoppingCart shoppingCartByUser = shoppingCartRepository.findByUser(user)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't get shopping cart by user id: " + user.getId())
                );
        return shoppingCartMapper.toDto(shoppingCartByUser);
    }
}
