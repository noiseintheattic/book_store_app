package mate.academy.service;

import mate.academy.dto.cart.CartItemDto;

public interface CartItemService {
    CartItemDto add(String email, Long bookId, int quantity);

    CartItemDto update(String email, Long cartItemId, int quantity);

    void delete(String email, Long cartItemId);
}
