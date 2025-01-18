package mate.academy.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mate.academy.config.MapperConfig;
import mate.academy.dto.book.BookDto;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.book.CreateBookRequestDto;
import mate.academy.model.Book;
import mate.academy.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = CategoryMapper.class)
public interface BookMapper {

    @Mapping(source = "booksCategories",
            target = "categories",
            qualifiedByName = "categoriesFromModel")
    BookDto toDto(Book book);

    @Mapping(source = "categories",
            target = "booksCategories",
            qualifiedByName = "categoriesFromRequest")
    Book toModel(CreateBookRequestDto createBookRequestDto);

    void updateFromDto(CreateBookRequestDto createBookRequestDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @Named("categoriesFromModel")
    default List<String> categoriesFromModel(Set<Category> booksCategories) {
        List<String> categories = new ArrayList<>();
        for (Category c : booksCategories) {
            categories.add(c.getName());
        }
        return categories;
    }

    @Named("categoriesFromRequest")
    default Set<Category> categoriesFromRequest(List<String> categoriesFromDto) {
        Set<Category> categories = new HashSet<>();
        for (String s : categoriesFromDto) {
            categories.add(new Category(s));
        }
        return categories;
    }

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        Set<Category> booksCategories = book.getBooksCategories();
        List<String> bookDtoCategories = new ArrayList<>();
        for (Category c : booksCategories) {
            bookDtoCategories.add(c.getName());
        }
    }
}
