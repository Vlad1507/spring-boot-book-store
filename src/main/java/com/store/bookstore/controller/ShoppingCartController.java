package com.store.bookstore.controller;

import com.store.bookstore.dto.cart.CartDto;
import com.store.bookstore.dto.cart.CartItemDto;
import com.store.bookstore.dto.cart.CartItemRequestDto;
import com.store.bookstore.dto.cart.UpdateCartItemRequestDto;
import com.store.bookstore.models.User;
import com.store.bookstore.services.cart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "", description = "")
@RestController
@RequestMapping(name = "/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('')")
    @GetMapping
    @Operation(summary = "", description = "")
    public CartDto getShoppingCart(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCart(user, pageable);
    }

    @PreAuthorize("hasRole('')")
    @PostMapping
    @Operation(summary = "", description = "")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemDto addItemToShoppingCart(
            @RequestBody @Valid CartItemRequestDto cartItemRequestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addItemToCart(user, cartItemRequestDto);
    }

    @PreAuthorize("hasRole('')")
    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "", description = "")
    @ResponseStatus(HttpStatus.OK)
    public CartItemDto updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestBody @Valid UpdateCartItemRequestDto updateCartItemRequestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateCartItemQuantity(user, itemId, updateCartItemRequestDto);
    }

    @PreAuthorize("hasRole('')")
    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "", description = "")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemFromShoppingCart(@PathVariable Long itemId,
                                           Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.deleteItemFromCartById(user, itemId);
    }
}
