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
import com.store.bookstore.dto.book.BookDto;
import com.store.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.store.bookstore.dto.book.BookSearchParametersDto;
import com.store.bookstore.dto.book.CreateUpdateBookRequestDto;
import com.store.bookstore.dto.exception.ExceptionDto;
import com.store.bookstore.dto.exception.ValidationExceptionDto;
import com.store.bookstore.util.BookUtil;
import java.math.BigDecimal;
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

@Sql(scripts = {"classpath:database/books/delete_all_books.sql",
        "classpath:database/categories/delete_all_categories.sql",
        "classpath:database/categories/insert_2_categories.sql",
        "classpath:database/books/insert_2_books.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database/books/delete_all_books.sql",
        "classpath:database/categories/delete_all_categories.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private CreateUpdateBookRequestDto createBookRequestDto;
    private BookDto bookDto;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        createBookRequestDto = BookUtil.getBookRequestDto();
        bookDto = BookUtil.getBookDto();
    }

    @Sql(scripts = "classpath:database/books/delete_third_book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should create book with valid data and return book dto")
    void createBook_AddBookToDB_StatusCreated() throws Exception {
        BookDto expected = BookUtil.getThirdBookDto();
        CreateUpdateBookRequestDto createBookRequestDto = BookUtil.getThirdBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);

        MvcResult mvcResult = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                BookDto.class
        );
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(actual, expected, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create book with invalid data throws an error")
    void createBook_AddInvalidIsbnBookToDB_StatusBadRequest() throws Exception {
        String fieldName = "isbn";
        CreateUpdateBookRequestDto requestDto = createBookRequestDto;
        requestDto.setIsbn("1234578956");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ValidationExceptionDto.class);
        ValidationExceptionDto expected = new ValidationExceptionDto(
                HttpStatus.BAD_REQUEST,
                Set.of(fieldName + " " + "Expected 123-4-56789-123-4 format of ISBN code"));
        assertEquals(actual.errorMessages(), expected.errorMessages());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return pages with books from a database in response as a book dto")
    void getAll_ReceiveAllBooksFromDB_StatusOk() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );

        List<BookDtoWithoutCategoryIds> actual = objectMapper
                .readValue(objectMapper.writeValueAsString(response.get("content")),
                        new TypeReference<>() {
                        });
        List<BookDtoWithoutCategoryIds> expected = BookUtil.listBookDtoWithoutCategory();
        System.out.println(actual);
        System.out.println(expected);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return book dto in response if id is valid")
    void getBookById_ReceiveBookByValidId_StatusOk() throws Exception {
        BookDto expected = BookUtil.getSecondBookDto();

        MvcResult mvcResult = mockMvc.perform(get("/books/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto response = objectMapper.readValue(
                        mvcResult.getResponse().getContentAsString(),
                        BookDto.class
                );
        EqualsBuilder.reflectionEquals(response, expected);
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should throw an error if requested book with an invalid id")
    void getBookById_ReceiveBookByNotValidId_StatusNotFound() throws Exception {
        long invalidId = 43L;

        MvcResult mvcResult = mockMvc.perform(get("/books/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        ExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ExceptionDto.class
        );
        System.out.println(actual);
        ExceptionDto expected = new ExceptionDto(
                HttpStatus.NOT_FOUND,
                "Can't get a book by id: " + invalidId);
        EqualsBuilder.reflectionEquals(actual, expected, "localDateTime");
    }

    @Sql(scripts = "classpath:database/books/update_first_book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should update a book and return the book dto if valid data was presented")
    void update_ShouldUpdateExistedBook_StatusOk() throws Exception {
        long id = 1L;
        createBookRequestDto.setTitle("The Call of the Wild");
        createBookRequestDto.setPrice(BigDecimal.valueOf(100));
        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/books/" + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                BookDto.class);

        BookDto expected = bookDto;
        expected.setTitle("The Call of the Wild");
        expected.setPrice(BigDecimal.valueOf(100));
        EqualsBuilder.reflectionEquals(actual, expected, "categoryIds");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should throw an error if trying to update a book by the invalid id")
    void update_UpdateBookWithInvalidId_StatusNotFound() throws Exception {
        long invalidId = 43L;
        createBookRequestDto.setTitle("The Call of the Wild");
        createBookRequestDto.setPrice(BigDecimal.valueOf(100));
        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/books/" + invalidId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                ExceptionDto.class);
        ExceptionDto expected = new ExceptionDto(
                HttpStatus.NOT_FOUND,
                "Can't get a book by id: " + invalidId);
        EqualsBuilder.reflectionEquals(actual, expected);
    }

    @Sql(scripts = "classpath:database/books/recover_first_book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should delete a book by existed id")
    void deleteById_DeleteBookById_StatusNoContent() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Should throw an error if trying ot delete a book by an invalid id")
    void deleteById_DeleteBookByInvalidId_StatusNotFound() throws Exception {
        long invalidId = 99L;

        MvcResult mvcResult = mockMvc.perform(delete("/books/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                ExceptionDto.class);
        ExceptionDto expected = new ExceptionDto(HttpStatus.NOT_FOUND,
                "Can't find book by id: " + invalidId);
        EqualsBuilder.reflectionEquals(actual, expected, "localDateTime");
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    @DisplayName("Should return a book dto by valid params")
    void searchBooks_FindBookByParams_StatusOk() throws Exception {
        BookSearchParametersDto parametersDto = new BookSearchParametersDto(
                "White Fang",
                "Jack London",
                "978-1-85813-740-7"
        );
        String jsonRequest = objectMapper.writeValueAsString(parametersDto);

        MvcResult mvcResult = mockMvc.perform(get("/books/search")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        List<BookDtoWithoutCategoryIds> expected = List.of(BookUtil.getBookDtoWithoutCategories());
        assertEquals(response, expected);
    }
}
