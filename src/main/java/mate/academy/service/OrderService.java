package mate.academy.service;

import java.util.Set;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.model.Order;

public interface OrderService {
    Set<OrderDto> getAllOrders();

    OrderDto placeOrder(Long shoppingCartId, String shippingAddress);

    String updateOrderStatus(Long orderId, Order.Status status);

    Set<OrderItemDto> getAllOrderItems(Long orderId);

    OrderItemDto getItem(Long orderId, Long itemId);
}
