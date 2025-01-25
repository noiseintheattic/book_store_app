package mate.academy.dto.cart;

import jakarta.validation.constraints.NotNull;

public record CartItemRequestDto(
        @NotNull
        Long bookId,
        int quantity
) {
}
