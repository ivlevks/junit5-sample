package org.example.junit.service;

import org.example.junit.dto.User;
import org.example.junit.extension.GlobalExtension;
import org.example.junit.extension.UserServiceParamResolver;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({
        UserServiceParamResolver.class,
        GlobalExtension.class
})
public class UserServiceTest {
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "222");
    private UserService userService;

    public UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    void init() {
        System.out.println("Before all " + this);
    }

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each " + this);
        this.userService = userService;
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
    //тест отключен
    @Disabled("flaky, need to see")
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

    // тест повторяется 3 раза
    @RepeatedTest(value = 3, name = RepeatedTest.LONG_DISPLAY_NAME)
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

    @Nested
    @Tag("login")
    @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
    class LoginTest {

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
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("Ivan", null))
            );
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

        @ParameterizedTest(name = "{arguments} test")
//        @ArgumentsSource()
//        @NullSource
//        @EmptySource
//        @ValueSource(strings = {
//                "Ivan", "Petr"
//        })
        @MethodSource("org.example.junit.service.UserServiceTest#getArgumentsForLoginTest")
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);
            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }
    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "222", Optional.of(PETR)),
                Arguments.of("Petr", "dummy", Optional.empty()),
                Arguments.of("dummy", "222", Optional.empty())
        );
    }

}
