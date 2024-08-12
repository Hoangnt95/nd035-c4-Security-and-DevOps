package com.example.demo.controller;

import com.example.demo.TestUtil;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestCartController {

    private CartController cartController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController(userRepo, cartRepo, itemRepo);
        TestUtil.injectObject(cartController, "cartRepository", cartRepo);
        TestUtil.injectObject(cartController, "userRepository", userRepo);
        TestUtil.injectObject(cartController, "itemRepository", itemRepo);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("testt");
        user.setPassword("testtPassword");
        user.setCart(cart);
        when(userRepo.findByUsername("testt")).thenReturn(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        BigDecimal price = new BigDecimal("1.95");
        item.setPrice(price);
        item.setDescription("Item 1 Description");
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
    }

    @Test
    public void testAddItemSuccess() {
        ModifyCartRequest cartRequest = mockCartRequest("testt", 1L, 1);
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals( BigDecimal.valueOf(1.95), cart.getTotal());
    }

    @Test
    public void testAddItemFailureByInvalidUser() {
        ModifyCartRequest cartRequest = mockCartRequest("udacity", 1L, 1);;
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testAddItemFailureByInvalidItem() {
        ModifyCartRequest cartRequest = mockCartRequest("testt", 5L, 1);
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveItemSuccess() {
        ModifyCartRequest cartRequest = mockCartRequest("testt", 1L, 6);
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        cartRequest = mockCartRequest("testt", 1L, 2);
        response = cartController.removeFromcart(cartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
    }

    public static ModifyCartRequest mockCartRequest(String userName, Long idItem, int quantity) {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(userName);
        cartRequest.setItemId(idItem);
        cartRequest.setQuantity(quantity);
        return cartRequest;
    }
}

