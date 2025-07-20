package com.store.bookstore.util;

import com.store.bookstore.dto.book.BookDto;
import com.store.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.store.bookstore.dto.book.CreateUpdateBookRequestDto;
import com.store.bookstore.models.Book;
import com.store.bookstore.models.Category;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class BookUtil {

    public static Book getBook(CreateUpdateBookRequestDto bookRequestDto) {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor(bookRequestDto.getAuthor());
        book.setTitle(bookRequestDto.getTitle());
        book.setIsbn(bookRequestDto.getIsbn());
        book.setPrice(bookRequestDto.getPrice());
        book.setDescription(bookRequestDto.getDescription());
        book.setCoverImage(bookRequestDto.getCoverImage());
        book.setCategories(Set.of(getCategory()));
        return book;
    }

    public static @NonNull Category getCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("adventure");
        category.setDescription("long exciting journeys books");
        return category;
    }

    public static CreateUpdateBookRequestDto getBookRequestDto() {
        CreateUpdateBookRequestDto createUpdateBookRequestDto = new CreateUpdateBookRequestDto();
        createUpdateBookRequestDto.setAuthor("Jack London");
        createUpdateBookRequestDto.setTitle("White Fang");
        createUpdateBookRequestDto.setIsbn("978-1-85813-740-7");
        createUpdateBookRequestDto.setPrice(BigDecimal.valueOf(40));
        createUpdateBookRequestDto.setDescription("An adventure novel about the journey"
                + " of a wild wolf dog to domestication in Canada.");
        createUpdateBookRequestDto.setCoverImage("https://upload.wikimedia.org/wikipedia/"
                + "commons/1/14/JackLondonwhitefang1.jpg");
        createUpdateBookRequestDto.setCategoryIds(Set.of(getCategory().getId()));
        return createUpdateBookRequestDto;
    }

    public static CreateUpdateBookRequestDto getInvalidBookRequestDto() {
        CreateUpdateBookRequestDto createUpdateBookRequestDto = new CreateUpdateBookRequestDto();
        createUpdateBookRequestDto.setAuthor("Jack London");
        createUpdateBookRequestDto.setTitle("White Fang");
        createUpdateBookRequestDto.setIsbn("9781-85813-740-7"); //invalid
        createUpdateBookRequestDto.setPrice(BigDecimal.valueOf(40));
        createUpdateBookRequestDto.setDescription("An adventure novel about the journey "
                + "of a wild wolf dog to domestication in Canada.");
        createUpdateBookRequestDto.setCoverImage("https://upload.wikimedia.org/wikipedia/"
                + "commons/1/14/JackLondonwhitefang1.jpg");
        createUpdateBookRequestDto.setCategoryIds(Set.of(getCategory().getId()));
        return createUpdateBookRequestDto;
    }

    public static BookDto getBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setAuthor("Jack London");
        bookDto.setTitle("White Fang");
        bookDto.setIsbn("978-1-85813-740-7");
        bookDto.setPrice(BigDecimal.valueOf(40));
        bookDto.setDescription("An adventure novel about the journey"
                + " of a wild wolf dog to domestication in Canada.");
        bookDto.setCoverImage("https://upload.wikimedia.org/wikipedia/commons/1/14/"
                + "JackLondonwhitefang1.jpg");
        bookDto.setCategoryIds(Set.of(getCategory().getId()));
        return bookDto;
    }

    public static Pageable getPageable() {
        return PageRequest.of(0,10, Sort.by("title"));
    }

    public static BookDtoWithoutCategoryIds getBookDtoWithoutCategories() {
        return new BookDtoWithoutCategoryIds(
                1L,
                "White Fang",
                "Jack London",
                "978-1-85813-740-7",
                new BigDecimal("40.00"),
                "An adventure novel about the journey "
                        + "of a wild wolf dog to domestication in Canada.",
                "https://upload.wikimedia.org/wikipedia/commons/1/14/"
                        + "JackLondonwhitefang1.jpg"
                );
    }

    public static List<BookDtoWithoutCategoryIds> listBookDtoWithoutCategory() {
        BookDtoWithoutCategoryIds firstBook = new BookDtoWithoutCategoryIds(
                1L,
                "White Fang",
                "Jack London",
                "978-1-85813-740-7",
                BigDecimal.valueOf(40.0),
                "An adventure novel about the journey of"
                        + " a wild wolf dog to domestication in Canada.",
                "https://upload.wikimedia.org/wikipedia/commons/1/14/"
                        + "JackLondonwhitefang1.jpg"
        );
        BookDtoWithoutCategoryIds secondBook = new BookDtoWithoutCategoryIds(
                2L,
                "The Intelligent Investor",
                "Benjamin Graham",
                "978-0-06055-566-5",
                BigDecimal.valueOf(64.0),
                "The book provides strategies on how to successfully use value investing"
                        + " in the stock market.",
                "https://upload.wikimedia.org/wikipedia/en/5/57/"
                        + "Theintelligentinvestor.jpg"
        );
        return List.of(firstBook, secondBook);
    }

    public static List<BookDtoWithoutCategoryIds> listBookDtoSameCategory() {
        BookDtoWithoutCategoryIds firstBook = new BookDtoWithoutCategoryIds(
                1L,
                "White Fang",
                "Jack London",
                "978-1-85813-740-7",
                BigDecimal.valueOf(40.0),
                "An adventure novel about the journey of "
                        + "a wild wolf dog to domestication in Canada.",
                "https://upload.wikimedia.org/wikipedia/commons/1/14/"
                        + "JackLondonwhitefang1.jpg"
        );
        BookDtoWithoutCategoryIds thirdBookDto = new BookDtoWithoutCategoryIds(
                3L,
                "Les Misérables",
                "Victor Hugo",
                "978-1-60710-816-0",
                BigDecimal.valueOf(45.0),
                "The novel describes the life of convict Jean Valjean,"
                        + " who has reformed and embarked on the path of goodness,"
                        + " but his past continues to haunt him.",
                "https://m.media-amazon.com/images/I/41CNEKXgt5L.jpg");
        return List.of(firstBook, thirdBookDto);
    }

    public static BookDto getSecondBookDto() {
        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(2L);
        secondBookDto.setTitle("The Intelligent Investor");
        secondBookDto.setAuthor("Benjamin Graham");
        secondBookDto.setIsbn("978-0-06055-566-5");
        secondBookDto.setPrice(BigDecimal.valueOf(64));
        secondBookDto.setDescription("The book provides strategies on how to successfully use"
                + " value investing in the stock market.");
        secondBookDto.setCoverImage("https://upload.wikimedia.org/wikipedia/en/5/57/"
                + "Theintelligentinvestor.jpg");
        secondBookDto.setCategoryIds(Set.of(2L));
        return secondBookDto;
    }

    public static BookDto getThirdBookDto() {
        BookDto thirdBookDto = new BookDto();
        thirdBookDto.setId(3L);
        thirdBookDto.setTitle("Les Misérables");
        thirdBookDto.setAuthor("Victor Hugo");
        thirdBookDto.setIsbn("978-1-60710-816-0");
        thirdBookDto.setPrice(BigDecimal.valueOf(45));
        thirdBookDto.setDescription("The novel describes the life of convict Jean Valjean,"
                + " who has reformed and embarked on the path of goodness,"
                + " but his past continues to haunt him.");
        thirdBookDto.setCoverImage("https://m.media-amazon.com/images/I/41CNEKXgt5L.jpg");
        thirdBookDto.setCategoryIds(Set.of(getCategory().getId()));
        return thirdBookDto;
    }

    public static CreateUpdateBookRequestDto getThirdBookRequestDto() {
        BookDto bookDto = getThirdBookDto();
        CreateUpdateBookRequestDto createBookRequestDto = new CreateUpdateBookRequestDto();
        createBookRequestDto.setTitle(bookDto.getTitle());
        createBookRequestDto.setAuthor(bookDto.getAuthor());
        createBookRequestDto.setIsbn(bookDto.getIsbn());
        createBookRequestDto.setPrice(bookDto.getPrice());
        createBookRequestDto.setDescription(bookDto.getDescription());
        createBookRequestDto.setCoverImage(bookDto.getCoverImage());
        createBookRequestDto.setCategoryIds(bookDto.getCategoryIds());
        return createBookRequestDto;
    }
}
