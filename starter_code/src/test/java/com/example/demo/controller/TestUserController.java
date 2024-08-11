package com.example.demo.controller;

import com.example.demo.TestUtil;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TestUserController {

    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController(userRepository, cartRepository, encoder);
        TestUtil.injectObject(userController, "userRepository", userRepository);
        TestUtil.injectObject(userController, "cartRepository", cartRepository);
    }

    public static CreateUserRequest mockCreateUserRequest(String username, String password, String confPassword) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(confPassword);
        return createUserRequest;
    }

    @Test
    public void testCreateUser() {
        when(encoder.encode("passwordTest")).thenReturn("passwordHashed");
        CreateUserRequest createUserRequest = mockCreateUserRequest("udacity", "passwordTest","passwordTest");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(createUserRequest.getUsername(), user.getUsername());
        assertEquals(0, user.getId());
    }

    @Test
    public void testfindById() {
        when(encoder.encode("passwordTest")).thenReturn("passwordHashed");
        CreateUserRequest createUserRequest = mockCreateUserRequest("udacity", "passwordTest","passwordTest");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User user = response.getBody();
        when(userRepository.findById(0L)).thenReturn(Optional.ofNullable(user));

        ResponseEntity<User> response2 = userController.findById(0L);

        User user2 = response2.getBody();
        assertNotNull(user2);
        assertEquals(createUserRequest.getUsername(), user2.getUsername());
        assertEquals("passwordHashed", user2.getPassword());
    }

    @Test
    public void testfindByName() {
        when(encoder.encode("passwordTest")).thenReturn("passwordHashed");
        CreateUserRequest createUserRequest = mockCreateUserRequest("udacity", "passwordTest","passwordTest");
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User user = response.getBody();
        when(userRepository.findByUsername("udacity")).thenReturn(user);

        ResponseEntity<User> response2 = userController.findByUserName("udacity");

        User user2 = response2.getBody();
        assertNotNull(user2);
        assertEquals(createUserRequest.getUsername(), user2.getUsername());
        assertEquals("passwordHashed", user2.getPassword());
    }

}
