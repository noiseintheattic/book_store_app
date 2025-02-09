package mate.academy.service;

import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.dto.category.CategoryDto;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Book;
import mate.academy.model.Category;
import mate.academy.repository.CategoryRepository;
import mate.academy.repository.book.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void getById_WithNonExisingBook_ShouldThrowException() {
        // Given
        Long bookId = 100L;
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getById(bookId)
        );
        // Then
        String expected = "Can't find book with id = " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(bookId);
    }

    @Test
    void getById_WithValidBook_ShouldReturnBook() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Winnie the Pooh");
        book.setAuthor("A.A.Milne");
        book.setPrice(BigDecimal.valueOf(5.99));
        book.setBooksCategories(Set.of());

        BookDtoWithoutCategoryIds expected = new BookDtoWithoutCategoryIds()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setPrice(book.getPrice());

        Mockito.when(bookRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDtoWithoutCategories(Mockito.any(Book.class))).thenReturn(expected);
        // When
        BookDtoWithoutCategoryIds actual = bookService.getById(bookId);
        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(bookId);
    }

    @Test
    void add_WithValidBookRequestDto_ShouldReturnBookDto() {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Winnie the Pooh")
                .setAuthor("A.A.Milne")
                .setPrice(BigDecimal.valueOf(4.55))
                .setCategories(List.of());

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setPrice(requestDto.getPrice());
        book.setBooksCategories(Set.of());

        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setPrice(requestDto.getPrice())
                .setCategories(requestDto.getCategories());

        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);

        // When
        BookDto actual = bookService.add(requestDto);

        // Then
        assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    public void findAll_ValidPageable_ReturnsAllBooks() {
        // Given
        Book book = new Book();
        book.setId(1L);
        book.setTitle("The Trial");
        book.setAuthor("Franz Kafka");
        book.setPrice(BigDecimal.valueOf(10.11));
        book.setBooksCategories(Set.of());

        BookDto bookDto = new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setPrice(book.getPrice())
                .setCategories(List.of());

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, 5);

        Mockito.when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        List<BookDto> actual = bookService.findAll(pageable);

        // Then
        assertEquals(1,actual.size());
        assertEquals(bookDto,actual.get(0));

        Mockito.verify(bookRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(bookMapper, Mockito.times(1)).toDto(book);
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    void update_WithValidBookRequestDto_ShouldReturnBookDto() {
        // Given
        Book existingBook = new Book();
        existingBook.setId(3L);
        existingBook.setTitle("The Trial");
        existingBook.setAuthor("Franz Kafka");
        existingBook.setPrice(BigDecimal.valueOf(77.77));
        existingBook.setBooksCategories(Set.of());

        BigDecimal updatedPrice = BigDecimal.valueOf(88.88);

        CreateBookRequestDto bookDtoToUpdate = new CreateBookRequestDto()
                .setTitle(existingBook.getTitle())
                .setAuthor(existingBook.getAuthor())
                .setPrice(updatedPrice)
                .setCategories(List.of());

        BookDto expected = new BookDto()
                .setTitle(bookDtoToUpdate.getTitle())
                .setAuthor(bookDtoToUpdate.getAuthor())
                .setPrice(bookDtoToUpdate.getPrice())
                .setCategories(bookDtoToUpdate.getCategories());

        Mockito.when(bookRepository.findById(Mockito.any())).thenReturn(Optional.of(existingBook));
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(existingBook);
        Mockito.when(bookMapper.toDto(Mockito.any(Book.class))).thenReturn(expected);

        // When
        BookDto actual = bookService.update(3L, bookDtoToUpdate);

        // Then
        assertNotNull(actual);
        assertEquals(updatedPrice, actual.getPrice());

        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(Mockito.any());
        Mockito.verify(bookRepository, Mockito.times(1))
                .save(Mockito.any(Book.class));
        Mockito.verify(bookMapper, Mockito.times(1))
                .toDto(Mockito.any(Book.class));
    }
}
