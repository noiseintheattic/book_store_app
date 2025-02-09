package mate.academy.repository;

import mate.academy.model.Order;
import mate.academy.model.User;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Check if returns correct orders set, when"
            + "passing existing user.")
    void findByUserEmail_CorrectUserEmail_ReturnsOrder() {
        // Given
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("Test");
        userRepository.save(user);

        Order order = new Order();
        order.setTotal(BigDecimal.TEN);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.NEW);
        order.setShippingAddress("Colorful 8");
        orderRepository.save(order);
        Set<Order> expectedOrders = new HashSet<>();
        expectedOrders.add(order);

        Set<Order> actualOrders = orderRepository.findByUserEmail("test@test.com");

        assertEquals(1, actualOrders.size());


    }

}