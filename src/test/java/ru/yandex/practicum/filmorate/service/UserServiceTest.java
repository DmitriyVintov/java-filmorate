package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private static Validator validator;
    private static UserService userService;
    private static User user1;
    private static User user2;
    private static User user3;

    @BeforeAll
    public static void setValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setUp() {
        userService = new UserService(new InMemoryUserStorage());
        user1 = new User("email@yandex.ru", "login1", LocalDate.parse("1917-11-07"));
        user2 = new User("email@yandex.ru", "login2", LocalDate.parse("1917-11-07"));
        user3 = new User("email@yandex.ru", "login3", LocalDate.parse("1917-11-07"));
    }

    @Test
    @DisplayName("Создание валидного пользователя")
    void shouldBeCreateFilm() {
        userService.createUser(user1);
        assertEquals(1, userService.getUsers().size());
    }

    @Test
    @DisplayName("Проверка валидации пользователя, когда email пустой")
    void shouldGetViolationWhenEmailIsBlank() {
        User user = new User("", "login", LocalDate.parse("1917-11-07"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Проверка валидации пользователя, когда login пустой")
    void shouldGetViolationWhenLoginIsBlank() {
        User user = new User("email@yandex.ru", "", LocalDate.parse("1917-11-07"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Проверка валидации пользователя, когда дата рождения в будущем")
    void shouldGetViolationWhenDateOfBirthInTheFuture() {
        User user = new User("email@yandex.ru", "login", LocalDate.parse("2117-11-07"));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Проверка установки имени пользователя как логина, когда имя не задано")
    void shouldGetNameAsLoginWhenNameIsEmpty() {
        User user = new User("email@yandex.ru", "login", LocalDate.parse("1917-11-07"));
        userService.createUser(user);
        assertEquals("login", userService.getUsers().get(0).getName());
    }

    @Test
    @DisplayName("Обновление валидного пользователя")
    void shouldBeUpdatedUser() {
        userService.createUser(user1);
        User user = new User("email@yandex.ru", "loginUpd", LocalDate.parse("1917-11-07"));
        user.setId(1);
        userService.updateUser(user);
        assertEquals("loginUpd", userService.getUsers().get(0).getName());
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void shouldGetUserById() {
        User user = userService.createUser(user1);
        assertEquals(user, userService.getUserById(user.getId()));
    }

    @Test
    @DisplayName("Получение списка пользователей")
    void shouldGetListOfFilms() {
        userService.createUser(user1);
        assertEquals(1, userService.getUsers().size());
    }

    @Test
    @DisplayName("Получение пустого списка пользователей, если еще не создано ни одного пользователя")
    void shouldGetEmptyListOfFilms() {
        assertEquals(0, userService.getUsers().size());
    }

    @Test
    @DisplayName("Удаление пользователя по id")
    void deleteUserById() {
        User user_1 = userService.createUser(user1);
        assertEquals(1, userService.getUsers().size());
        userService.deleteUserById(user_1.getId());
        assertEquals(0, userService.getUsers().size());
    }

    @Test
    @DisplayName("Добавление друзей пользователю")
    void addingToFriends() {
        User user_1 = userService.createUser(user1);
        User user_2 = userService.createUser(user2);
        assertEquals(0, userService.getFriends(user_1.getId()).size());
        userService.addingToFriends(user_1.getId(), user_2.getId());
        assertEquals(1, userService.getFriends(user_1.getId()).size());
    }

    @Test
    @DisplayName("Удаление из друзей пользователя")
    void deletingFromFriends() {
        User user_1 = userService.createUser(user1);
        User user_2 = userService.createUser(user2);
        userService.addingToFriends(user_1.getId(), user_2.getId());
        assertEquals(1, userService.getFriends(user_1.getId()).size());
        userService.deletingFromFriends(user_1.getId(), user_2.getId());
        assertEquals(0, userService.getFriends(user_1.getId()).size());
    }

    @Test
    @DisplayName("Вывод списка друзей пользователя")
    void getFriends() {
        User user_1 = userService.createUser(user1);
        User user_2 = userService.createUser(user2);
        assertEquals(0, userService.getFriends(user_1.getId()).size());
        user1.setFriends(user_2.getId());
        assertEquals(1, userService.getFriends(user_1.getId()).size());
    }

    @Test
    @DisplayName("Вывод общих друзей двух пользователей")
    void getCommonFriends() {
        User user_1 = userService.createUser(user1);
        User user_2 = userService.createUser(user2);
        User user_3 = userService.createUser(user3);
        user_1.setFriends(user_2.getId());
        user_1.setFriends(user_3.getId());
        user_2.setFriends(user_1.getId());
        user_2.setFriends(user_3.getId());
        assertTrue(userService.getCommonFriends(user_1.getId(), user_2.getId()).contains(user_3));
        assertEquals(1, userService.getCommonFriends(user_1.getId(), user_2.getId()).size());
    }
}