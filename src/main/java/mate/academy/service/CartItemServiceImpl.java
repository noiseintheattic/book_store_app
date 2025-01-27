package mate.academy.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.mapper.CartItemMapper;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.repository.BookRepository;
import mate.academy.repository.CartItemRepository;
import mate.academy.repository.ShoppingCartRepository;
import mate.academy.repository.UserRepository;
import mate.academy.security.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional
    public CartItemDto add(Long bookId, Integer quantity) {
        String email = authenticationService.getAuthenticatedUserEmail();
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException("Cant find book by id: " + bookId));

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserEmail(email)
                .orElse(new ShoppingCart(userRepository.findUserByEmail(email).orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find user by email: " + email)
                )));

        if (shoppingCart.getCartItems()
                .stream()
                .noneMatch(i -> i.getBook().getId().equals(bookId))) {

            CartItem cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setQuantity(quantity);
            cartItem.setShoppingCart(shoppingCart);
            Set<CartItem> cartItems = shoppingCart.getCartItems();
            cartItems.add(cartItem);
            cartItemRepository.save(cartItem);
            shoppingCart.setCartItems(cartItems);
            shoppingCartRepository.save(shoppingCart);
            return cartItemMapper.toDto(cartItem);
        } else {
            Set<CartItem> cartItems = shoppingCart.getCartItems();
            cartItems.stream()
                            .filter(i -> i.getBook().getId().equals(bookId))
                                    .findFirst()
                                            .ifPresent(i -> i.setQuantity(quantity));
            shoppingCart.setCartItems(cartItems);
            shoppingCartRepository.save(shoppingCart);
            CartItem cartItem = cartItems.stream()
                    .filter(item -> item.getBook().getId().equals(bookId))
                    .findFirst()
                    .orElseThrow();
            return cartItemMapper.toDto(cartItem);
        }
    }

    @Override
    public CartItemDto update(Long cartItemId, Integer quantity) {
        String email = authenticationService.getAuthenticatedUserEmail();
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUserEmail(email)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find shoppingCart for user: " + email));
        Set<CartItem> cartItems = shoppingCart.getCartItems();

        CartItem cartItem = cartItems
                .stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find cartItem by id: " + cartItemId));

        cartItem.setQuantity(quantity);
        cartItems.add(cartItem);
        cartItemRepository.save(cartItem);
        shoppingCart.setCartItems(cartItems);
        shoppingCartRepository.save(shoppingCart);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public void delete(Long cartItemId) {
        String email = authenticationService.getAuthenticatedUserEmail();
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUserEmail(email)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find shoppingCart for user: " + email));
        Set<CartItem> cartItems = shoppingCart.getCartItems();

        CartItem cartItem = cartItems
                .stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find cartItem by id: " + cartItemId));

        cartItems.remove(cartItem);
        shoppingCart.setCartItems(cartItems);
        shoppingCartRepository.save(shoppingCart);
        cartItemRepository.delete(cartItem);
    }
}
