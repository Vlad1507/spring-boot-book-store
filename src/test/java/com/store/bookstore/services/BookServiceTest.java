package com.store.bookstore.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.store.bookstore.dto.book.BookDto;
import com.store.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.store.bookstore.dto.book.BookSearchParametersDto;
import com.store.bookstore.dto.book.CreateUpdateBookRequestDto;
import com.store.bookstore.exception.EntityNotFoundException;
import com.store.bookstore.mapper.BookMapper;
import com.store.bookstore.models.Book;
import com.store.bookstore.repository.book.BookRepository;
import com.store.bookstore.repository.book.BookSpecificationBuilder;
import com.store.bookstore.repository.category.CategoryRepository;
import com.store.bookstore.services.book.BookServiceImpl;
import com.store.bookstore.util.BookUtil;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final Long BOOK_ID = 1L;

    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookSpecificationBuilder specificationBuilder;
    @Mock
    private Specification<Book> specification;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private CreateUpdateBookRequestDto requestDto;
    private CreateUpdateBookRequestDto invalidRequestDto;
    private BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds;
    private Pageable pageable;
    private BookDto bookDto;

    @BeforeEach
    public void setUp() {
        requestDto = BookUtil
                .getBookRequestDto();
        book = BookUtil.getBook(requestDto);
        invalidRequestDto = BookUtil
                .getInvalidBookRequestDto();
        bookDtoWithoutCategoryIds = BookUtil.getBookDtoWithoutCategories();
        pageable = BookUtil.getPageable();
        bookDto = BookUtil.getBookDto();
    }

    @Test
    @DisplayName("Save the book from request dto. Should return Dto of saved book")
    void save_saveCorrectBook_ShouldSaveBook() {
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.save(requestDto);

        assertEquals(bookDto, actual);
    }

    @Test
    @DisplayName("Should throw an error when request dto has invalid ISBN number format")
    void save_saveInvalidIsbnValueBook_ShouldThrowError() {
        String expected = "Expected 123-4-56789-123-4 format of ISBN code";
        when(bookMapper.toModel(invalidRequestDto))
                .thenThrow(new ValidationException(expected));

        Exception actual = assertThrows(ValidationException.class,
                () -> bookService.save(invalidRequestDto)
        );

        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should return paginated list of book dto")
    void findAll_searchValidBooks_ShouldReturnBooks() {
        Page<Book> page = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAll(pageable)).thenReturn(page);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        Page<BookDtoWithoutCategoryIds> actual = bookService.findAll(pageable);
        Page<BookDtoWithoutCategoryIds> expected = new PageImpl<>(
                List.of(bookDtoWithoutCategoryIds), pageable, 1);
        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("Search book by valid ID. Should return the book")
    void findById_SearchByValidId_ShouldReturnBook() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.findById(1L);

        assertEquals(bookDto, actual);
    }

    @Test
    @DisplayName("Should throw an error while searching by invalid ID")
    void findById_SearchByInvalidId_ShouldReturnException() {
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.empty());
        String expected = "Can't get a book by id: " + BOOK_ID;

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(BOOK_ID));

        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should update existed book")
    void update_UpdateExistedBook_ShouldUpdateBook() {
        CreateUpdateBookRequestDto updateBookRequestDto = requestDto;
        updateBookRequestDto.setDescription("An adventure novel");
        updateBookRequestDto.setPrice(BigDecimal.valueOf(100));
        updateBookRequestDto.setCoverImage("absent");
        BookDto expected = new BookDto();
        expected.setId(BOOK_ID);
        expected.setAuthor("Jack London");
        expected.setTitle("White Fang");
        expected.setIsbn("978-1-85813-740-7");
        expected.setPrice(BigDecimal.valueOf(100));
        expected.setDescription("An adventure novel");
        expected.setCoverImage("absent");
        expected.setCategoryIds(Set.of(1L));

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        doNothing().when(bookMapper).updateBookFromDto(updateBookRequestDto, book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.update(1L, updateBookRequestDto);
        assertEquals(expected, actual);
        verify(bookMapper, times(1))
                .updateBookFromDto(updateBookRequestDto, this.book);
    }

    @Test
    @DisplayName("Should throw an error while trying to update a book with an invalid id.")
    void update_UpdateWithInvalidId_ShouldThrowError() {
        Long invalidId = 100L;
        CreateUpdateBookRequestDto updateBookRequestDto = requestDto;
        updateBookRequestDto.setDescription("An adventure novel");
        updateBookRequestDto.setPrice(BigDecimal.valueOf(100));
        updateBookRequestDto.setCoverImage("absent");
        String expected = "Can't get a book by id: " + invalidId;

        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(invalidId, updateBookRequestDto));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should delete a book with an existed id")
    void deleteById_DeleteExistedBook_ShouldDeleteBook() {
        when(bookRepository.existsById(BOOK_ID)).thenReturn(true);

        bookService.deleteById(BOOK_ID);

        verify(bookRepository, times(1)).deleteById(BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Should throw an error while trying to delete a book with an invalid id.")
    void deleteById_DeleteBookWithInvalidId_ShouldThrowError() {
        Long invalidId = 100L;
        String expected = "Can't find book by id: " + invalidId;
        when(bookRepository.existsById(invalidId)).thenReturn(false);

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteById(invalidId));

        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Should return books by specific parameters")
    void searchBooks_SearchBookWithValidParams_ShouldReturnBooks() {
        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto(
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn()
        );
        when(specificationBuilder.build(bookSearchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable))
                .thenReturn(new PageImpl<>(List.of(book)));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        List<BookDtoWithoutCategoryIds> dtoWithoutCategoryIds = bookService
                .searchBooks(bookSearchParametersDto, pageable);

        assertNotNull(dtoWithoutCategoryIds);
        assertEquals(1, dtoWithoutCategoryIds.size());
        verify(specificationBuilder).build(bookSearchParametersDto);
    }

    @Test
    @DisplayName("Should return a paginated list of books by category id")
    void getBooksByCategoryId_SearchWithValidCategoryId_ShouldReturnBooks() {
        Long categoryId = 1L;
        Page<Book> page = new PageImpl<>(List.of(book), pageable, 1);
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(bookRepository.findAllByCategoriesContains(any(Set.class),eq(pageable)))
                .thenReturn(page);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        Page<BookDtoWithoutCategoryIds> actual = bookService
                .getBooksByCategoryId(categoryId, pageable);
        Page<BookDtoWithoutCategoryIds> expected = new PageImpl<>(
                List.of(bookDtoWithoutCategoryIds), pageable, 1);
        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("Should throw an error if the category by the presented id doesn't exist")
    void getBooksByCategoryId_SearchWithInvalidCategoryId_ShouldReturnError() {
        Long categoryInvalidId = 911L;
        when(categoryRepository.existsById(categoryInvalidId)).thenReturn(false);

        Exception actual = assertThrows(EntityNotFoundException.class,
                () -> bookService.getBooksByCategoryId(categoryInvalidId, pageable));
        String expected = "Can't find category by id: " + categoryInvalidId;

        assertEquals(actual.getMessage(), expected);
    }
}
