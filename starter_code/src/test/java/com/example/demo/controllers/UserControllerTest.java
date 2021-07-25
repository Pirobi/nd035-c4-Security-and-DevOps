package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController unitUnderTest;

    @Mock
    UserRepository userRepo;

    @Mock
    CartRepository cartRepo;

    @Mock
    BCryptPasswordEncoder encoder;

    @Before
    public void startUp(){
        MockitoAnnotations.initMocks(this);
        unitUnderTest = new UserController();
        TestUtils.InjectObjects(unitUnderTest, "userRepository", userRepo);
        TestUtils.InjectObjects(unitUnderTest, "cartRepository", cartRepo);
        TestUtils.InjectObjects(unitUnderTest, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void testUserLogin() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        when(userRepo.save(Mockito.any())).thenReturn(TestUtils.createUser());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        final ResponseEntity<User> response = unitUnderTest.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(1, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());

        when(userRepo.findByUsername("test")).thenReturn(TestUtils.createUser());

        ResponseEntity<User> getResponse = unitUnderTest.findByUserName("test");
        assertNotNull(getResponse);
        assertEquals(200, getResponse.getStatusCodeValue());

        User user = getResponse.getBody();
        assertNotNull(user);

        assertEquals(u.getId(), user.getId());
        assertEquals(u.getUsername(), user.getUsername());
        assertEquals(u.getPassword(), user.getPassword());
    }

    @Test
    public void testPasswordNotLongEnough(){
        //Test password length minimum
        CreateUserRequest r = createUser("test", "tttt", "tttt");

        final ResponseEntity<User> response = unitUnderTest.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testPasswordMismatch(){
        CreateUserRequest r = createUser("test1", "testPassword", "expectToFail");
        final ResponseEntity<User> response = unitUnderTest.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testNoUserFound(){
        ResponseEntity<User> response = unitUnderTest.findByUserName("test");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        assertNull(response.getBody());
    }

    private CreateUserRequest createUser(String username, String pass, String confirm){
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(username);
        r.setPassword(pass);
        r.setConfirmPassword(confirm);
        return r;
    }
}
