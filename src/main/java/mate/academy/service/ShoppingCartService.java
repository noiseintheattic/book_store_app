package mate.academy.service;

import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.model.Book;

public interface ShoppingCartService {
    ShoppingCartDto findByEmail(String email);

    ShoppingCartDto add(String email, Book book);

    ShoppingCartDto update(String email);
}
