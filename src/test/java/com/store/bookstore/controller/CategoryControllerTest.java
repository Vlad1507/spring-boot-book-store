package com.store.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.store.bookstore.dto.category.CategoryDto;
import com.store.bookstore.dto.category.CategoryRequestDto;
import com.store.bookstore.dto.exception.ExceptionDto;
import com.store.bookstore.dto.exception.ValidationExceptionDto;
import com.store.bookstore.util.BookUtil;
import com.store.bookstore.util.CategoryUtil;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = {"classpath:database/categories/delete_all_categories.sql",
        "classpath:database/categories/insert_2_categories.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/categories/delete_all_categories.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private CategoryDto categoryDto;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        categoryDto = CategoryUtil.getCategoryDto();
    }

    @Sql(scripts = "classpath:database/categories/remove_third_category_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should create category with valid data and return category dto")
    void createCategory_AddCategoryToDB_StatusCreated() throws Exception {
        CategoryDto expected = CategoryUtil.getThirdCategoryDto();
        CategoryRequestDto requestDto = CategoryUtil.getThirdCategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryDto.class);

        System.out.println(actual);
        System.out.println(expected);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertEquals(actual, expected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create category with invalid data should throws an error")
    void createCategory_AddBlankNameCategoryToDB_StatusBadRequest() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("", "null");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ValidationExceptionDto.class);

        ValidationExceptionDto expected = new ValidationExceptionDto(
                HttpStatus.BAD_REQUEST,
                Set.of("name must not be blank"));
        EqualsBuilder.reflectionEquals(actual, expected, "localDateTime");
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return pages with categories from a database"
            + " in response as a category dto")
    void getAll_ReceiveAllCategoriesFromDB_StatusOk() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/categories/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Object> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        List<CategoryDto> actual = objectMapper.readValue(
                objectMapper.writeValueAsString(response),
                new TypeReference<>() {
                });
        List<CategoryDto> expected = CategoryUtil.getCategoryDtoList();
        assertEquals(actual, expected);
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return category dto in response if id is valid")
    void getCategoryById_ReceiveCategoryById_StatusOk() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryDto.class);
        CategoryDto expected = categoryDto;
        assertEquals(actual, expected);
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should throw an error if requested category with an invalid id")
    void getCategoryById_ReceiveCategoryByInvalidId_StatusNotFound() throws Exception {
        long invalidId = 99L;

        MvcResult mvcResult = mockMvc.perform(get("/categories/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto expected = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ExceptionDto.class);
        ExceptionDto actual = new ExceptionDto(
                HttpStatus.NOT_FOUND,
                "Can't find a category by id: " + invalidId);
        EqualsBuilder.reflectionEquals(expected, actual, "localDateTime");
    }

    @Sql(scripts = "classpath:database/categories/update_first_category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should update a category and return the category dto "
            + "if valid data was presented")
    void updateCategory_UpdateExistedCategory_StatusOk() throws Exception {
        CategoryRequestDto updateRequestDto = new CategoryRequestDto(
                "horror",
                "spooky books"
        );
        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/categories/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryDto.class);
        CategoryDto expected = new CategoryDto(1L, "horror", "spooky books");
        assertEquals(actual, expected);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should throw an error if trying to update a category by the invalid id")
    void updateCategory_UpdateCategoryInvalidId_StatusNotFound() throws Exception {
        long invalidId = 99L;
        CategoryRequestDto updateRequestDto = new CategoryRequestDto(
                "horror",
                "spooky books"
        );
        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/categories/" + invalidId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ExceptionDto.class);
        ExceptionDto expected = new ExceptionDto(
                HttpStatus.NOT_FOUND,
                "Can't find a category by id: " + invalidId);
        EqualsBuilder.reflectionEquals(expected, actual, "localDateTime");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should throw an error if trying to update a book with a blank name field")
    void updateCategory_UpdateCategoryValidationFailed_StatusBadRequest() throws Exception {
        long id = 1L;
        CategoryRequestDto updateRequestDto = new CategoryRequestDto(
                "",
                "spooky books"
        );
        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/categories/" + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ValidationExceptionDto.class);
        ValidationExceptionDto expected = new ValidationExceptionDto(
                HttpStatus.BAD_REQUEST,
                Set.of("name must not be blank"));
        EqualsBuilder.reflectionEquals(expected, actual, "localDateTime");
    }

    @Sql(scripts = "classpath:database/categories/recover_first_category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should delete a category by existed id")
    void deleteCategory_RemoveExistedCategory_StatusNoContent() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should throw an error if trying ot delete a category by an invalid id")
    void deleteCategory_InvalidCategoryId_StatusNotFound() throws Exception {
        long invalidId = 99L;

        MvcResult mvcResult = mockMvc.perform(delete("/categories/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ExceptionDto.class);
        ExceptionDto expected = new ExceptionDto(
                HttpStatus.NOT_FOUND,
                "Can't find a category by id: " + invalidId);
        EqualsBuilder.reflectionEquals(actual, expected, "localDateTime");
    }

    @Sql(scripts = {"classpath:database/books/insert_2_books.sql",
            "classpath:database/books/insert_third_book.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/books/delete_third_book.sql",
            "classpath:database/books/delete_all_books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return a list of books by a valid category id")
    void getBooksByCategoryId_ReceiveBook_StatusOk() throws Exception {
        long categoryId = 1;

        MvcResult mvcResult = mockMvc.perform(get("/categories/" + categoryId + "/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        List<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(
                objectMapper.writeValueAsString(response.get("content")),
                new TypeReference<>() {
                });
        List<BookDtoWithoutCategoryIds> expected = BookUtil.listBookDtoSameCategory();
        assertEquals(expected, actual);
    }

    @Sql(scripts = {"classpath:database/books/insert_2_books.sql",
            "classpath:database/books/insert_third_book.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/books/delete_third_book.sql",
            "classpath:database/books/delete_all_books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should throw an error if a category id is invalid")
    void getBooksByCategoryId_InvalidCategoryId_StatusNotFound() throws Exception {
        long invalidId = 99L;

        MvcResult mvcResult = mockMvc.perform(get("/categories/" + invalidId + "/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ExceptionDto.class);
        ExceptionDto expected = new ExceptionDto(HttpStatus.NOT_FOUND,
                "Can't find category by id: " + invalidId);
        EqualsBuilder.reflectionEquals(actual, expected, "localDateTime");
    }
}
