package org.example.junit.service;

import org.example.junit.dto.User;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "222");
    private UserService userService;

    @BeforeAll
    void init() {
        System.out.println("Before all " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each " + this);
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNoUsersAdded() {
        System.out.println("Test1 " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void userSizeIfUserAdded() {
        System.out.println("Test2 " + this);
        userService.add(IVAN);
        userService.add(PETR);

        var users = userService.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void loginSuccessIfUserExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertEquals(IVAN, user));
    }

    @Test
    void logicFailIfPasswordIncorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "wrong password");

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void logicFailIfUserNotExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("not Ivan", "wrong password");

        assertTrue(maybeUser.isEmpty());
    }

    @AfterEach
    void deleteData() {
        System.out.println("After each " + this);
    }

}
