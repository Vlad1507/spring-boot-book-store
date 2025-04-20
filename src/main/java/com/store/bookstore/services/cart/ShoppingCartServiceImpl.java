package com.store.bookstore.services.cart;

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
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getShoppingCartWithItems(User user) {
        ShoppingCart shoppingCartByUser = getShoppingCart(user);
        return shoppingCartMapper.toDto(shoppingCartByUser);
    }

    @Override
    public ShoppingCartDto addItemToCart(User user, CartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCartByUser = getShoppingCart(user);
        CartItem cartItem = cartItemMapper.toModel(cartItemRequestDto);
        cartItem.setShoppingCart(shoppingCartByUser);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(shoppingCartByUser);
    }

    @Override
    public ShoppingCartDto updateCartItemQuantity(
            User user, Long itemId,
            UpdateCartItemRequestDto updateCartItemRequestDto) {
        ShoppingCart shoppingCartByUser = getShoppingCart(user);
        CartItem cartItemFromDb = getCartItem(itemId, shoppingCartByUser);
        cartItemFromDb.setQuantity(updateCartItemRequestDto.quantity());
        cartItemRepository.save(cartItemFromDb);
        return shoppingCartMapper.toDto(shoppingCartByUser);
    }

    @Override
    public void deleteItemFromCartById(User user, Long itemId) {
        ShoppingCart shoppingCartByUser = getShoppingCart(user);
        CartItem cartItemFromDb = getCartItem(itemId, shoppingCartByUser);
        shoppingCartByUser.dismissCartItem(cartItemFromDb);
        cartItemFromDb.dismissShoppingCart();
        cartItemRepository.deleteById(cartItemFromDb.getId());
    }

    @Override
    public void createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart getShoppingCart(User user) {
        return shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't get shopping cart by user id: " + user.getId())
                );
    }

    private CartItem getCartItem(Long itemId, ShoppingCart shoppingCartByUser) {
        return cartItemRepository
                .findByIdAndShoppingCart_Id(itemId, shoppingCartByUser.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't get cart item from database by id: " + itemId
                        )
                );
    }
}
