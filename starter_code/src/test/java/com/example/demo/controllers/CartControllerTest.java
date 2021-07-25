package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    @Mock
    UserRepository userRepo;

    @Mock
    CartRepository cartRepo;

    @Mock
    ItemRepository itemRepo;

    private CartController unitUnderTest;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        unitUnderTest = new CartController();
        TestUtils.InjectObjects(unitUnderTest, "userRepository", userRepo);
        TestUtils.InjectObjects(unitUnderTest, "cartRepository", cartRepo);
        TestUtils.InjectObjects(unitUnderTest, "itemRepository", itemRepo);
    }

    @Test
    public void testGoPath(){
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("test");
        req.setItemId(1);
        req.setQuantity(1);

        when(userRepo.findByUsername("test")).thenReturn(TestUtils.createUser());
        when(itemRepo.findById(1L)).thenReturn(Optional.of(TestUtils.createItem()));

        ResponseEntity<Cart> response = unitUnderTest.addTocart(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart c = response.getBody();
        assertNotNull(c);
        assertEquals(1L, (long) c.getId());
        assertEquals(1, c.getItems().size());

        response = unitUnderTest.removeFromcart(req);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        c = response.getBody();
        assertNotNull(c);
        assertEquals(1L, (long) c.getId());
        assertEquals(0, c.getItems().size());
    }

    @Test
    public void testNoUser(){
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("test");
        req.setItemId(1);
        req.setQuantity(1);

        ResponseEntity<Cart> response = unitUnderTest.addTocart(req);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        assertNull(response.getBody());

        response = unitUnderTest.removeFromcart(req);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        assertNull(response.getBody());
    }

    @Test
    public void testNoItem(){
        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("test");
        req.setItemId(1);
        req.setQuantity(1);

        when(userRepo.findByUsername("test")).thenReturn(TestUtils.createUser());
        ResponseEntity<Cart> response = unitUnderTest.addTocart(req);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());


        response = unitUnderTest.removeFromcart(req);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
