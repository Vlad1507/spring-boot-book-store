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
