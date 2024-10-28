package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private Cart cart;
    private User user;
    private List<Item> items;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        cart = new Cart();
        cart.setId(1L);
        items = new ArrayList<>();

        Item item = new Item();
        item.setId(1L);
        item.setName("Sample Item");
        item.setPrice(BigDecimal.valueOf(10.0));
        item.setDescription("Sample description");
        items.add(item);

        cart.setItems(items);
        user.setCart(cart);
    }

    @Test
    public void testSubmitOrderSuccess() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(orderRepository, times(1)).save(any(UserOrder.class));
    }

    @Test
    public void testSubmitOrderUserNotFound() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("nonExistentUser");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        verify(orderRepository, never()).save(any(UserOrder.class));
    }

    @Test
    public void testGetOrdersForUserSuccess() {
        List<UserOrder> orders = new ArrayList<>();
        orders.add(UserOrder.createFromCart(cart));

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void testGetOrdersForUserUserNotFound() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("nonExistentUser");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        verify(orderRepository, never()).findByUser(any(User.class));
    }
}
