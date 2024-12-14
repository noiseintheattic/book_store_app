package mate.academy.book_store_app.service;

import mate.academy.book_store_app.model.Book;

import java.util.List;

public interface BookService {
    Book add(Book book);
    List<Book> findAll();
}
