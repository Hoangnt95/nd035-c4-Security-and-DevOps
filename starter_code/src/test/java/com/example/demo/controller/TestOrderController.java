package com.example.demo.controller;

import com.example.demo.TestUtil;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestOrderController {

    private OrderController orderController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController(userRepository, orderRepository);
        TestUtil.injectObject(orderController, "userRepository", userRepository);
        TestUtil.injectObject(orderController, "orderRepository", orderRepository);
    }
    @Test
    public void testSubmit() {
        User user = mockUser("test", "password", 0L);
        Item item = TestItemController.mockItem(0L, "Item", new BigDecimal("2.99"), "Description");
        Cart cart = mockCart(user, item);
        user.setCart(cart);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertNotNull(order.getTotal());
    }

    @Test
    public void testSubmitNullUser() {
        User user = mockUser("test", "password", 0L);
        Item item = TestItemController.mockItem(0L, "Item", new BigDecimal("2.99"), "Description");
        Cart cart = mockCart(user, item);
        user.setCart(cart);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrder() {
        User user = mockUser("test", "password", 0L);
        Item item = TestItemController.mockItem(0L, "Item", new BigDecimal("2.99"), "Description");
        Cart cart = mockCart(user, item);
        user.setCart(cart);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        orderController.submit(user.getUsername());

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    @Test
    public void testGetOrderNullUser() {
        User user = mockUser("test", "password", 0L);
        Item item = TestItemController.mockItem(0L, "Item", new BigDecimal("2.99"), "Description");
        Cart cart = mockCart(user, item);
        user.setCart(cart);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        orderController.submit(user.getUsername());

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    public static User mockUser(String username, String password, Long id) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setId(id);
        return user;
    }

    public static Cart mockCart(User user, Item item) {
        Cart cart = new Cart();
        cart.setId(0L);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        cart.setItems(itemList);
        cart.setTotal(new BigDecimal("2.99"));
        cart.setUser(user);
        return cart;
    }
}
