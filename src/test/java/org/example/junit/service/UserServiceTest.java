package org.example.junit.service;

import org.example.junit.dto.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

        //assertJ
        assertThat(users).isEmpty();
//        assertTrue(users.isEmpty());
    }

    @Test
    void userSizeIfUserAdded() {
        System.out.println("Test2 " + this);
        userService.add(IVAN);
        userService.add(PETR);

        var users = userService.getAll();

        //assertJ
        assertThat(users).hasSize(2);
        //Junit
//        assertEquals(2, users.size());
    }

    @Test
    void loginSuccessIfUserExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        //assertJ
        assertThat(maybeUser).isPresent();
        maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));

//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));
    }

    @Test
    void logicFailIfPasswordIncorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "wrong password");

        assertThat(maybeUser).isEmpty();

//        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void logicFailIfUserNotExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("not Ivan", "wrong password");

        assertThat(maybeUser).isEmpty();

//        assertTrue(maybeUser.isEmpty());
    }
    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedById();

        //hamcrest
        MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId()));

        //assertJ
        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );
    }

    @AfterEach
    void deleteData() {
        System.out.println("After each " + this);
    }

}
