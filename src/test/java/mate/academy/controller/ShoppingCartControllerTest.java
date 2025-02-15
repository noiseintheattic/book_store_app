package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.dto.cart.CartItemDto;
import mate.academy.dto.cart.CartItemRequestDto;
import mate.academy.dto.cart.ShoppingCartDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/book/add-three-default-books.sql"
                    ));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingCart/add-three-default-users.sql"
                    ));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingCart/add-six-default-cartItems.sql"
                    ));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingCart/add-three-default-shoppingCarts.sql"
                    ));
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/book/remove-all-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingCart/remove-all-cartItems-users-shoppingCarts.sql"
                    ));
        }
    }

    @DisplayName("Given shopping cart, check if return right shopping cart for user")
    @Test
    @WithMockUser(username = "blue@test.com", roles = {"ADMIN", "USER"})
    void getCart_existingShoppingCart_Success() throws Exception {
        // given
        CartItemDto cartItemDto = new CartItemDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("Harry Potter")
                .setQuantity(5);
        ShoppingCartDto expected = new ShoppingCartDto()
                .setId(1L)
                .setUserId(2L)
                .setCartItems(Set.of(cartItemDto));
        // when
        MvcResult result = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/api/cart")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), ShoppingCartDto.class);
        // then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @DisplayName("No shopping cart for requested user, check if return status Not Found")
    @Test
    @WithMockUser(username = "red@test.com", roles = {"ADMIN", "USER"})
    void getCart_NotExistingShoppingCart_ShouldReturnStatusNotFound() throws Exception {
        // when
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/cart")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Given valid request for adding cart item, check if item is correctly added.")
    @Test
    @WithMockUser(username = "blue@test.com", roles = {"ADMIN", "USER"})
    void add_ValidRequestDto_Success() throws Exception {
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(3L, 3);
        CartItemDto expected = new CartItemDto()
                .setBookId(3L)
                .setBookTitle("The Trial")
                .setQuantity(3);
        String jsonRequest = objectMapper.writeValueAsString(cartItemRequestDto);
        // when
        MvcResult result = mockMvc.perform(post("/api/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        // then
        CartItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartItemDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @DisplayName("No id value defined in request for adding item, "
            + "check if return status Bad Request")
    @Test
    @WithMockUser(username = "blue@test.com", roles = {"ADMIN", "USER"})
    void add_NullBookId_ShouldReturnBadRequest() throws Exception {
        // given
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(null, 3);
        String jsonRequest = objectMapper.writeValueAsString(cartItemRequestDto);
        // when / then
        mockMvc.perform(post("/api/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Trying to add not existing book in DB, "
            + "check if return status Not Found")
    @Test
    @WithMockUser(username = "blue@test.com", roles = {"ADMIN", "USER"})
    void add_NotExistingBook_ShouldReturnNotFound() throws Exception {
        // given
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(7L, 3);
        String jsonRequest = objectMapper.writeValueAsString(cartItemRequestDto);
        // when / then
        mockMvc.perform(post("/api/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Given valid request for updating quantity of cart item, "
            + "check if quantity will be updated")
    @Test
    @WithMockUser(username = "yellow@test.com", roles = {"ADMIN", "USER"})
    @Sql(scripts = "classpath:database/shoppingCart/remove-all-cartItems.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCart/add-six-default-cartItems.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_ValidCartItemIdAndQuantity_Success() throws Exception {
        Integer quantity = 10;
        CartItemDto expected = new CartItemDto()
                .setId(6L)
                .setBookId(3L)
                .setBookTitle("The Trial")
                .setQuantity(quantity);
        String jsonRequest = objectMapper.writeValueAsString(quantity);
        // when
        MvcResult result = mockMvc.perform(put("/api/cart/cart-items/6")
                        .content(jsonRequest)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        // then
        CartItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartItemDto.class);
        EqualsBuilder.reflectionEquals(expected, actual);
        assertEquals(expected, actual);
    }

    @DisplayName("Trying to update not existing cart item, "
            + "check if return status Not Found")
    @Test
    @WithMockUser(username = "yellow@test.com", roles = {"ADMIN", "USER"})
    void update_NotExistingCartItem_ShouldReturnNotFound() throws Exception {
        // given
        Integer quantity = 10;
        String jsonRequest = objectMapper.writeValueAsString(quantity);
        // when / then
        mockMvc.perform(put("/api/cart/cart-items/99")
                        .content(jsonRequest)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("Trying to updated quantity with negative value, "
            + "check if return status Bad Request")
    @Test
    @WithMockUser(username = "yellow@test.com", roles = {"ADMIN", "USER"})
    void update_NegativeQuantity_ShouldReturnBadRequest() throws Exception {
        Integer quantity = -10;
        String jsonRequest = objectMapper.writeValueAsString(quantity);
        // when / then
        mockMvc.perform(put("/api/cart/cart-items/99")
                        .content(jsonRequest)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Trying to delete item with given id, "
            + "check if return status No Content")
    @Test
    @WithMockUser(username = "yellow@test.com", roles = {"ADMIN", "USER"})
    @Sql(scripts = "classpath:database/shoppingCart/remove-all-cartItems.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/shoppingCart/add-six-default-cartItems.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ExistingId_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/cart/cart-items/6"))
                .andExpect(status().isNoContent());
    }
}
