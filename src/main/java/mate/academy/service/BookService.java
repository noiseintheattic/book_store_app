package mate.academy.service;

import java.util.List;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookSearchParametersDto;
import mate.academy.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto add(CreateBookRequestDto createBookRequestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto getById(Long id);

    void deleteById(Long id);

    BookDto update(Long id, CreateBookRequestDto createBookRequestDto);

    List<BookDto> search(Pageable pageable, BookSearchParametersDto params);
}
