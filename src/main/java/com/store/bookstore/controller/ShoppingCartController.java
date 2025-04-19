package com.store.bookstore.controller;

import com.store.bookstore.dto.cart.CartItemDto;
import com.store.bookstore.dto.cart.CartItemRequestDto;
import com.store.bookstore.dto.cart.ShoppingCartDto;
import com.store.bookstore.dto.cart.UpdateCartItemRequestDto;
import com.store.bookstore.models.User;
import com.store.bookstore.services.cart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "Cart management", description = "Endpoints for managing cart and items")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get shopping cart",
            description = "Return shopping cart and items in it")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCartWithItems(user);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(summary = "Add an item",
            description = "Allows add the item into shopping cart")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartDto addItemToShoppingCart(
            @RequestBody @Valid CartItemRequestDto cartItemRequestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addItemToCart(user, cartItemRequestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item quantity",
            description = "Allows to change quantity of items in shopping cart")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestBody @Valid UpdateCartItemRequestDto updateCartItemRequestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateCartItemQuantity(user, itemId, updateCartItemRequestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Delete item",
            description = "Remove item from shopping cart by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemFromShoppingCart(@PathVariable Long itemId,
                                           Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.deleteItemFromCartById(user, itemId);
    }
}
