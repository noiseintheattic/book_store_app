package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserEmail_GivenExistingShoppingCart_ShouldReturnShoppingCart() {
        // given
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("Test");
        userRepository.save(user);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(Set.of());
        shoppingCartRepository.save(shoppingCart);
        // when
        Optional<ShoppingCart> foundShoppingCart
                = shoppingCartRepository.findByUserEmail(user.getEmail());
        // then
        assertTrue(foundShoppingCart.isPresent());
        assertEquals("test@test.com", foundShoppingCart.get().getUser().getEmail());
        assertEquals(Set.of(), foundShoppingCart.get().getCartItems());
    }
}
