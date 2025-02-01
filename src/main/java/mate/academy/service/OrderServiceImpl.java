package mate.academy.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.order.OrderDto;
import mate.academy.dto.order.OrderItemDto;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.mapper.OrderItemMapper;
import mate.academy.mapper.OrderMapper;
import mate.academy.model.CartItem;
import mate.academy.model.Order;
import mate.academy.model.OrderItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.OrderItemRepository;
import mate.academy.repository.OrderRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.repository.UserRepository;
import mate.academy.security.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final AuthenticationService authenticationService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Set<OrderDto> getAllOrders() {
        String email = authenticationService.getAuthenticatedUserEmail();
        Set<Order> orders = orderRepository.findByUserEmail(email);
        Set<OrderDto> orderDtoSet = new HashSet<>();
        for (Order o : orders) {
            orderDtoSet.add(orderMapper.toDto(o));
        }
        return orderDtoSet;
    }

    @Override
    @Transactional
    public OrderDto placeOrder(Long shoppingCartId, String shippingAddress) {
        String email = authenticationService.getAuthenticatedUserEmail();
        User user = userRepository.findUserByEmail(email).orElseThrow();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserEmail(email).orElseThrow();
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setUser(user);
        order.setStatus(Order.Status.NEW);
        order.setShippingAddress(shippingAddress);
        Set<OrderItem> orderItems = new HashSet<>();
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem c : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(c.getQuantity());
            orderItem.setOrder(order);
            orderItem.setBook(c.getBook());
            BigDecimal price = c.getBook().getPrice();
            Integer quantity = c.getQuantity();
            BigDecimal amount = price.multiply(BigDecimal.valueOf(quantity));
            total = total.add(amount);
            orderItems.add(orderItem);
            orderItemRepository.save(orderItem);
        }

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public String updateOrderStatus(Long orderId, String status) {
        Order.Status statusFromString = null;
        try {
            statusFromString = Order.Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Can't find proper name of status for given string: "
                    + status, e);
        }
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (statusFromString != null) {
            order.setStatus(statusFromString);
        }
        orderRepository.save(order);
        return status.toString();
    }

    @Override
    public Set<OrderItemDto> getAllOrderItems(Long orderId) {
        String email = authenticationService.getAuthenticatedUserEmail();
        Set<Order> userOrders = orderRepository.findByUserEmail(email);
        Order order = null;
        for (Order o : userOrders) {
            if (o.getId().equals(orderId)) {
                order = orderRepository.findById(orderId).orElseThrow();
            }
        }
        if (order != null) {
            Set<OrderItem> orderItems = order.getOrderItems();
            Set<OrderItemDto> orderItemsDto = new HashSet<>();
            for (OrderItem o : orderItems) {
                orderItemsDto.add(orderItemMapper.toDto(o));
            }
            return orderItemsDto;
        }
        throw new EntityNotFoundException("Can't find items from order with id: "
        + orderId);
    }

    @Override
    public OrderItemDto getItem(Long orderId, Long itemId) {
        String email = authenticationService.getAuthenticatedUserEmail();
        Set<Order> userOrders = orderRepository.findByUserEmail(email);
        Order order = null;
        for (Order o : userOrders) {
            if (o.getId().equals(orderId)) {
                order = orderRepository.findById(orderId).orElseThrow();
            }
        }
        if (order != null) {
            OrderItem orderItem = orderItemRepository.findById(itemId).orElseThrow(
                    () -> new EntityNotFoundException("Can't find item with id: "
                            + itemId + " from order with id: " + orderId));

            return orderItemMapper.toDto(orderItem);
        }

        throw new EntityNotFoundException("Can't find order with id: "
                + orderId);
    }
}
