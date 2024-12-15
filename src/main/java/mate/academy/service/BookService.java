package mate.academy.service;

import java.util.List;
import mate.academy.model.Book;

public interface BookService {

    Book add(Book book);

    List<Book> findAll();
}
