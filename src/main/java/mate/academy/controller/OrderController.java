package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.dto.order.OrderRequestDto;
import mate.academy.dto.order.UpdateOrderStatusRequestDto;
import mate.academy.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders managment", description = "Endpoints for managing orders")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get all orders",
            description = "Get a list of user's all orders")
    public Set<OrderDto> getAllOrders(Authentication authentication) {
        return orderService.getAllOrders();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Place order",
            description = "Make an order for all items from shopping cart")
    public OrderDto placeOrder(@RequestBody OrderRequestDto requestDto) {
        return orderService.placeOrder(requestDto.shoppingCartId(),
                requestDto.shippingAddress());
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update status",
            description = "Update status of chosen order")
    public String updateOrderStatus(@PathVariable Long orderId,
                                    @RequestBody UpdateOrderStatusRequestDto statusRequestDto) {
        return orderService.updateOrderStatus(orderId, statusRequestDto.getStatus());
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get items of order",
            description = "Get a list of all items of chosen order")
    public Set<OrderItemDto> getAllOrderItems(@PathVariable Long orderId) {
        return orderService.getAllOrderItems(orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get item from order",
            description = "Get a chosen item from chosen order")
    public OrderItemDto getItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getItem(orderId, itemId);
    }

}
