INSERT INTO books(id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES(3,
    "Les Misérables",
    "Victor Hugo",
    "978-1-60710-816-0",
    45,
    "The novel describes the life of convict Jean Valjean, who has reformed and embarked on the path of goodness, but his past continues to haunt him.",
    "https://m.media-amazon.com/images/I/41CNEKXgt5L.jpg",
    FALSE);

INSERT INTO books_categories(book_id, category_id)
VALUES(3, 1);
