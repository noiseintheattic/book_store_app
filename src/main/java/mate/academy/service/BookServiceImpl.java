package mate.academy.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.BookDto;
import mate.academy.dto.BookSearchParametersDto;
import mate.academy.dto.CreateBookRequestDto;
import mate.academy.exceptions.DataProcessingException;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.mapper.BookMapper;
import mate.academy.model.Book;
import mate.academy.repository.BookRepository;
import mate.academy.repository.BookSpecificationBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto add(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toModel(createBookRequestDto);
        try {
            Book savedBook = bookRepository.save(book);
            return bookMapper.toDto(savedBook);
        } catch (RuntimeException e) {
            throw new DataProcessingException("Can't create a book: " + createBookRequestDto,e);
        }
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(b -> bookMapper.toDto(b))
                .toList();
    }

    @Override
    public BookDto getById(Long id) {
        Book bookById = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find book with id = " + id));
        return bookMapper.toDto(bookById);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto createBookRequestDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book with id: " + id));

        bookMapper.updateFromDto(createBookRequestDto, book);
        Book updatedBook = bookRepository.save(book);

        return bookMapper.toDto(updatedBook);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto params) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
