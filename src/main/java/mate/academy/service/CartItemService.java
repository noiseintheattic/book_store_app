package mate.academy.service;

import mate.academy.dto.cart.CartItemDto;

public interface CartItemService {
    CartItemDto add(Long bookId, Integer quantity);

    CartItemDto update(Long cartItemId, Integer quantity);

    void delete(Long cartItemId);
}
