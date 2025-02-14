package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.dto.cart.ShoppingCartDto;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.mapper.ShoppingCartMapper;
import mate.academy.model.Book;
import mate.academy.model.CartItem;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.repository.CartItemRepository;
import mate.academy.repository.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    private static final String email = "test@test.com";
    private static final Set<CartItemDto> cartItemsDto = new HashSet<>();
    private static Book book;
    private static CartItem cartItem;
    private static ShoppingCart shoppingCart;
    private static final Set<CartItem> cartItems = new HashSet<>();

    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartServiceImpl;

    @BeforeAll
    static void beforeAll() {
        Integer quantity = 1;
        book = new Book()
                .setId(1L)
                .setTitle("Winnie the Pooh")
                .setAuthor("A.A.Milne")
                .setPrice(BigDecimal.valueOf(5.99))
                .setBooksCategories(Set.of());
        cartItem = new CartItem()
                .setId(1L)
                .setBook(book)
                .setQuantity(quantity);

        CartItemDto cartItemDto = new CartItemDto()
                .setId(cartItem.getId())
                .setBookTitle(cartItem.getBook().getTitle())
                .setBookId(cartItem.getBook().getId())
                .setQuantity(cartItem.getQuantity());

        User user = new User()
                .setId(1L)
                .setEmail(email)
                .setPassword("password")
                .setFirstName("Test")
                .setLastName("Test")
                .setShippingAddress("Short 2")
                .setDeleted(false);

        Set<CartItemDto> cartItemsDto = new HashSet<>();
        cartItemsDto.add(cartItemDto);

        cartItems.add(cartItem);
        shoppingCart = new ShoppingCart()
                .setId(1L)
                .setUser(user)
                .setCartItems(cartItems);
        user.setShoppingCart(shoppingCart);
    }

    @Test
    void findByEmail_GivenExistingShoppingCart_ShouldReturnShoppingCartDto() {
        // given
        ShoppingCartDto expected = new ShoppingCartDto()
                .setId(shoppingCart.getId())
                .setUserId(shoppingCart.getUser().getId())
                .setCartItems(cartItemsDto);
        Mockito.when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        Mockito.when(shoppingCartRepository
                .findByUserEmail(email))
                .thenReturn(Optional.of(shoppingCart));
        // when
        Optional<ShoppingCartDto> actual = shoppingCartServiceImpl.findByEmail(email);
        // then
        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    void add_existingShoppingCart_ShouldReturnShoppingCartDto() {
        // given
        ShoppingCartDto expected = new ShoppingCartDto()
                .setId(shoppingCart.getId())
                .setUserId(shoppingCart.getUser().getId())
                .setCartItems(cartItemsDto);
        cartItem.setShoppingCart(shoppingCart);

        Mockito.when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(expected);
        Mockito.when(shoppingCartRepository.findByUserEmail(email))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(cartItemRepository.save(Mockito.any(CartItem.class)))
                .thenReturn(cartItem);
        Mockito.when(shoppingCartRepository.save(shoppingCart))
                .thenReturn(shoppingCart);
        // when
        ShoppingCartDto actual = shoppingCartServiceImpl.add(email, book);
        // then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void updateItemsSet_ExistingShoppingCart_ShouldReturnShoppingCartDto() {
        // given
        ShoppingCartDto expected = new ShoppingCartDto()
                .setId(shoppingCart.getId())
                .setUserId(shoppingCart.getUser().getId())
                .setCartItems(cartItemsDto);
        cartItem.setShoppingCart(shoppingCart);
        Mockito.when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(expected);
        Mockito.when(shoppingCartRepository.findByUserEmail(email))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(shoppingCartRepository.save(shoppingCart))
                .thenReturn(shoppingCart);
        // when
        ShoppingCartDto actual = shoppingCartServiceImpl.updateItemsSet(email);
        // then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void updateItemsSet_NotExistingShoppingCart_ShouldThrowEntityNotFoundException() {
        // given
        String notExistingUser = "notExisting@test.com";
        Mockito.when(shoppingCartRepository.findByUserEmail(notExistingUser))
                .thenReturn(Optional.empty());
        // when / then
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartServiceImpl.updateItemsSet(notExistingUser));
    }

    @Test
    void clearCartItems_ExistingShoppingCart_ShouldClearItems() {
        // given
        String email2 = "secondUser@test.com";
        CartItem cartItem1 = new CartItem();
        CartItem cartItem2 = new CartItem();
        Set<CartItem> items = new HashSet<>();
        items.add(cartItem1);
        items.add(cartItem2);

        ShoppingCart secondShoppingCart = new ShoppingCart()
                .setId(2L)
                .setUser(new User()
                        .setId(2L)
                        .setEmail(email2)
                        .setFirstName("Name")
                        .setLastName("LastName")
                        .setDeleted(false))
                .setCartItems(items);
        Mockito.when(shoppingCartRepository.findByUserEmail(email2))
                .thenReturn(Optional.of(shoppingCart));
        // when
        shoppingCartServiceImpl.clearItems(email2);
        // then
        assertTrue(shoppingCart.getCartItems().isEmpty());
    }

    @Test
    void clearItems_NotExistingShoppingCart_ShouldThrowEntityNotFoundException() {
        // given
        String notExistingUser = "notExisting@test.com";
        Mockito.when(shoppingCartRepository.findByUserEmail(notExistingUser))
                .thenReturn(Optional.empty());
        // when / then
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartServiceImpl.clearItems(notExistingUser));
    }

    @Test
    void findByEmail_NotExistingShoppingCart_ShouldThrowEntityNotFoundException() {
        // given
        String notExistingUser = "notExisting@test.com";
        Mockito.when(shoppingCartRepository.findByUserEmail(notExistingUser))
                .thenReturn(Optional.empty());
        // when / then
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartServiceImpl.findByEmail(notExistingUser));
    }
}
