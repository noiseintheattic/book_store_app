package mate.academy.controller;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public Set<OrderDto> getAllOrders(Authentication authentication) {
        return orderService.getAllOrders();
    }

    @PostMapping
    public OrderDto placeOrder(Long shoppingCartId, String shippingAddress) {
        return orderService.placeOrder(shoppingCartId, shippingAddress);
    }

    @PatchMapping("/{id}")
    public String updateOrderStatus(@PathVariable Long orderId, String status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @GetMapping("/{orderId}/items")
    public Set<OrderItemDto> getAllOrderItems(@PathVariable Long orderId) {
        return orderService.getAllOrderItems(orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getItem(Long orderId, Long itemId) {
        return getItem(orderId, itemId);
    }

}
