package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    @Mock
    UserRepository userRepo;

    @Mock
    OrderRepository orderRepo;

    private OrderController unitUnderTest;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        unitUnderTest = new OrderController();
        TestUtils.InjectObjects(unitUnderTest, "userRepository", userRepo);
        TestUtils.InjectObjects(unitUnderTest, "orderRepository", orderRepo);
    }

    @Test
    public void testGoPath(){
        User u = TestUtils.createUser();
        Item i = TestUtils.createItem();
        u.getCart().addItem(i);

        when(userRepo.findByUsername("test")).thenReturn(u);

        ResponseEntity<UserOrder> response = unitUnderTest.submit("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(BigDecimal.TEN, order.getTotal());
        assertEquals(order.getUser(), u);
        assertTrue(order.getItems().contains(i));
    }

    @Test
    public void testNoUser(){
        ResponseEntity<UserOrder> response = unitUnderTest.submit("test");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        assertNull(response.getBody());

        ResponseEntity<List<UserOrder>> response2 = unitUnderTest.getOrdersForUser("test");
        assertNotNull(response2);
        assertEquals(404, response2.getStatusCodeValue());

        assertNull(response2.getBody());
    }
}
