package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "book", target = "bookId", qualifiedByName = "bookToId")
    @Mapping(source = "book", target = "bookTitle", qualifiedByName = "bookToTitle")
    @Mapping(source = "quantity", target = "quantity")
    CartItemDto toDto(CartItem cartItem);

    @Named("bookToId")
    default Long bookToId(Book book) {
        return book.getId();
    }

    @Named("bookToTitle")
    default String bookToTitle(Book book) {
        return book.getTitle();
    }
}
