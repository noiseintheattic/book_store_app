package mate.academy.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
        @NotNull
        Long shoppingCartId,
        @NotNull
        @NotBlank
        String shippingAddress
) {
}
