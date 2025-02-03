package mate.academy.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.repository.CartItemRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.repository.UserRepository;
import mate.academy.security.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository repository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final AuthenticationService authenticationService;

    @Override
    public Optional<ShoppingCartDto> findByEmail(String email) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by email: " + email)
        );
        return Optional.ofNullable(shoppingCartMapper.toDto(shoppingCart));
    }

    @Override
    @Transactional
    public ShoppingCartDto add(String email, Book book) {
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(cartItem.getQuantity() + 1);

        if (shoppingCartRepository.findByUserEmail(email).isEmpty()) {
            ShoppingCart shoppingCart = new ShoppingCart();
            cartItem.setShoppingCart(shoppingCart);
            cartItemRepository.save(cartItem);
            shoppingCart.getCartItems().add(cartItem);
            shoppingCartRepository.save(shoppingCart);
            return shoppingCartMapper.toDto(shoppingCart);
        }

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find ShoppingCart for email: " + email));

        cartItemRepository.save(cartItem);
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateItemsSet(String email) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUserEmail(email)
                .orElseThrow();
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void clearItems(String email) {
        ShoppingCart shoppingCartByEmail
                = shoppingCartRepository.findByUserEmail(email).orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find Shopping Cart by email: "
                        + email));
        shoppingCartByEmail.getCartItems().clear();
    }
}
