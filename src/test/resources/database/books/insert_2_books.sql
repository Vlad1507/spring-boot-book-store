INSERT INTO books(id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES(1,
        "White Fang",
        "Jack London",
        "978-1-85813-740-7",
        40,
        "An adventure novel about the journey of a wild wolf dog to domestication in Canada.",
        "https://upload.wikimedia.org/wikipedia/commons/1/14/JackLondonwhitefang1.jpg",
        FALSE);

INSERT INTO books_categories(book_id, category_id)
VALUES(1, 1);

INSERT INTO books(id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES(2,
        "The Intelligent Investor",
        "Benjamin Graham",
        "978-0-06055-566-5",
        64,
        "The book provides strategies on how to successfully use value investing in the stock market.",
        "https://upload.wikimedia.org/wikipedia/en/5/57/Theintelligentinvestor.jpg",
        FALSE);

INSERT INTO books_categories(book_id, category_id)
VALUES(2, 2);
