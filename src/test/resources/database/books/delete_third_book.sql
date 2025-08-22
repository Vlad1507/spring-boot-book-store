DELETE FROM books_categories
    WHERE book_id = (SELECT id FROM books WHERE title = 'Les Misérables');
DELETE FROM books
    WHERE id = 3;
ALTER TABLE books AUTO_INCREMENT = 3;
