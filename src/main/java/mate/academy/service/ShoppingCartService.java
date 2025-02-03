package mate.academy.service;

import java.util.Optional;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.model.Book;

public interface ShoppingCartService {
    Optional<ShoppingCartDto> findByEmail(String email);

    ShoppingCartDto add(String email, Book book);

    ShoppingCartDto updateItemsSet(String email);

    void clearItems(String email);
}
