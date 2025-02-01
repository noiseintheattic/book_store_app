package mate.academy.dto.order;

public record OrderRequestDto(
        Long shoppingCartId,
        String shippingAddress
) {
}
