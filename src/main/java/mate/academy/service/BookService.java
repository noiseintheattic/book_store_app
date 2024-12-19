package mate.academy.service;

import java.util.List;
import mate.academy.dto.BookDto;
import mate.academy.dto.CreateBookRequestDto;

public interface BookService {

    BookDto add(CreateBookRequestDto createBookRequestDto);

    List<BookDto> findAll();

    BookDto getById(Long id);
}
