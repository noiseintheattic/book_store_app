package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.dto.cart.CartItemRequestDto;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.service.CartItemService;
import mate.academy.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart items manager", description = "Endpoints managing the Shopping Cart.")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get all cart items",
            description = "Get a list of items from the shopping cart")
    public ShoppingCartDto getCart(Authentication authentication) {
        return shoppingCartService.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("Can't find cart for user: "
                + authentication.getName())
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Add new item",
            description = "Add new item to shopping cart")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemDto add(@Valid @RequestBody CartItemRequestDto cartItemRequestDto) {
        return cartItemService.add(
                cartItemRequestDto.bookId(),
                cartItemRequestDto.quantity());
    }

    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Update quantity",
            description = "Update quantity of the item from shopping cart")
    public CartItemDto update(@PathVariable Long cartItemId,
                              @RequestBody @Valid @Min(0) Integer quantity) {
        return cartItemService.update(cartItemId, quantity);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Delete item", description = "Delete item from shopping cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long cartItemId) {
        cartItemService.delete(cartItemId);
    }

}
