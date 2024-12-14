package mate.academy.book_store_app.repository;

import mate.academy.book_store_app.model.Book;

import java.util.List;

public interface BookRepository {
    Book add(Book book);
    List<Book> findAll();
}
