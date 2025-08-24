package com.store.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.bookstore.dto.cart.CartItemRequestDto;
import com.store.bookstore.dto.cart.CartItemWithBookTitleDto;
import com.store.bookstore.dto.cart.ShoppingCartDto;
import com.store.bookstore.dto.cart.UpdateCartItemRequestDto;
import com.store.bookstore.dto.exception.ExceptionDto;
import com.store.bookstore.models.User;
import com.store.bookstore.util.ShoppingCartUtil;
import com.store.bookstore.util.UserUtil;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = {"classpath:database/books/delete_all_books.sql",
        "classpath:database/categories/delete_all_categories.sql",
        "classpath:database/categories/insert_2_categories.sql",
        "classpath:database/books/insert_2_books.sql",
        "classpath:database/users/insert_user.sql",},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
        "classpath:database/books/delete_all_books.sql",
        "classpath:database/categories/delete_all_categories.sql",
        "classpath:database/users/delete_user.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    private static MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    public ShoppingCartControllerTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(scripts = {"classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            "classpath:database/shoppingCarts/insert_shopping_cart_wih_2_cart_items.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return shopping cart of user")
    void getShoppingCart_GetShoppingCartOfUser_StatusOk() throws Exception {
        ShoppingCartDto expected = ShoppingCartUtil.getShoppingCartWithTwoItemsDto();
        User user = UserUtil.getUserFromDb();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        MvcResult mvcResult = mockMvc.perform(get("/cart")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ShoppingCartDto.class
        );
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should throw an error due to invalid user id")
    void getShoppingCart_InvalidUserId_StatusNotFound() throws Exception {
        User user = UserUtil.getUserFromDb();
        user.setId(99L);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        MvcResult mvcResult = mockMvc.perform(get("/cart")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ExceptionDto.class
        );
        ExceptionDto expected = new ExceptionDto(HttpStatus.NOT_FOUND,
                "Can't get shopping cart by user id: " + user.getId());
        assertTrue(EqualsBuilder.reflectionEquals(actual, expected, "localDateTime"));
    }

    @Sql(scripts = {"classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            "classpath:database/shoppingCarts/insert_shopping_cart_wih_one_cart_items.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should add item to the user's shopping cart")
    void addItemToShoppingCart_AddItemToShoppingCart_StatusCreated() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(2L, 25);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        User user = UserUtil.getUserFromDb();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        MvcResult mvcResult = mockMvc.perform(post("/cart")
                        .with(authentication(auth))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ShoppingCartDto.class
        );
        CartItemWithBookTitleDto firstItem = ShoppingCartUtil.getCartItemWithBookTitleDto();
        CartItemWithBookTitleDto secondItem =
                new CartItemWithBookTitleDto(2L, 2L, "The Intelligent Investor", 25);
        ShoppingCartDto expected =
                new ShoppingCartDto(1L, user.getId(), Set.of(firstItem, secondItem));
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFieldsOfTypes(ShoppingCartDto.class)
                .isEqualTo(expected);
        assertThat(actual.cartItems())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected.cartItems());
    }

    @Sql(scripts = {"classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            "classpath:database/shoppingCarts/insert_shopping_cart_wih_2_cart_items.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should throw an error due to invalid book id")
    void addItemToShoppingCart_InvalidBookId_StatusNotFound() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(99L, 15);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        User user = UserUtil.getUserFromDb();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        MvcResult mvcResult = mockMvc.perform(post("/cart")
                        .with(authentication(auth))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ExceptionDto.class
        );
        ExceptionDto expected = new ExceptionDto(HttpStatus.NOT_FOUND,
                "Can't find a book by id: " + requestDto.bookId());
        assertTrue(EqualsBuilder.reflectionEquals(actual, expected, "localDateTime"));
    }

    @Sql(scripts = {"classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            "classpath:database/shoppingCarts/insert_shopping_cart_wih_one_cart_items.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should update quantity of item in shopping cart")
    void updateCartItemQuantity_ShouldUpdateItemQuantity_StatusOk() throws Exception {
        User user = UserUtil.getUserFromDb();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        UpdateCartItemRequestDto updateRequestDto = new UpdateCartItemRequestDto(30);
        long itemId = 1L;
        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/cart/items/" + itemId)
                        .with(authentication(auth))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ShoppingCartDto.class);

        CartItemWithBookTitleDto cartItem =
                new CartItemWithBookTitleDto(1L, 1L, "White Fang", 30);
        ShoppingCartDto expected = new ShoppingCartDto(1L, user.getId(), Set.of(cartItem));
        assertEquals(expected, actual);
    }

    @Sql(scripts = {"classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            "classpath:database/shoppingCarts/insert_shopping_cart_wih_one_cart_items.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should throw an error due to invalid item id")
    void updateCartItemQuantity_InvalidItemId_StatusNotFound() throws Exception {
        User user = UserUtil.getUserFromDb();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        long itemId = 99L;
        UpdateCartItemRequestDto updateRequestDto = new UpdateCartItemRequestDto(7);
        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/cart/items/" + itemId)
                        .with(authentication(auth))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ExceptionDto.class);
        ExceptionDto expected = new ExceptionDto(HttpStatus.NOT_FOUND,
                "Can't get cart item from database by id: " + itemId);
        assertTrue(EqualsBuilder.reflectionEquals(actual, expected, "localDateTime"));
    }

    @Sql(scripts = {"classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            "classpath:database/shoppingCarts/insert_shopping_cart_wih_one_cart_items.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should delete an existing item")
    void deleteItemFromShoppingCart_DeleteExistingItem_StatusNoContent() throws Exception {
        User user = UserUtil.getUserFromDb();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        long itemId = 1L;

        mockMvc.perform(delete("/cart/items/" + itemId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Sql(scripts = {"classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            "classpath:database/shoppingCarts/insert_shopping_cart_wih_one_cart_items.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCarts/delete_shopping_cart_and_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should throw an error due to invalid item id")
    void deleteItemFromShoppingCart_DeleteNonExistedItem_StatusNotFound() throws Exception {
        User user = UserUtil.getUserFromDb();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        long itemId = 99L;

        MvcResult mvcResult = mockMvc.perform(delete("/cart/items/" + itemId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ExceptionDto.class);
        ExceptionDto expected = new ExceptionDto(HttpStatus.NOT_FOUND,
                "Can't get cart item from database by id: " + itemId);
        assertTrue(EqualsBuilder.reflectionEquals(actual, expected, "localDateTime"));
    }

}
