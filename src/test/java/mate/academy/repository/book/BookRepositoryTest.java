package mate.academy.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    void findById_GivenExistingBook_ShouldReturnBook() {
        // given
        Book book = new Book();
        book.setTitle("Testing Book");
        bookRepository.save(book);
        // when
        Optional<Book> foundBook = bookRepository.findById(book.getId());
        // then
        assertTrue(foundBook.isPresent());
        assertEquals("Testing Book", foundBook.get().getTitle());
    }
}
