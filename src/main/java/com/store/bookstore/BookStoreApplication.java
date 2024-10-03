package com.store.bookstore;

import com.store.bookstore.models.Book;
import com.store.bookstore.services.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("White Fang");
            book.setAuthor("Jack London");
            book.setDescription("It is a novel about a wild wolfdog's adventure "
                    + "during the 1890s Klondike Gold Rush.");
            book.setCoverImage(
                    "https://upload.wikimedia.org/wikipedia/commons/1/14/JackLondonwhitefang1.jpg"
            );
            book.setIsbn("978-1-85813-740-7");
            book.setPrice(BigDecimal.valueOf(20));

            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
