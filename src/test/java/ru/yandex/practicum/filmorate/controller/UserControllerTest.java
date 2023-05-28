package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static Validator validator;
    private static UserController controller;
    private static User user;

    @BeforeAll
    public static void setValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setController() {
        controller = new UserController();
        user = new User("email@yandex.ru", "login", LocalDate.parse("1917-11-07"));
    }

    @Test
    @DisplayName("Создание валидного пользователя")
    void shouldBeCreateFilm() {
        controller.createUser(user);
        assertEquals(1, controller.getUsers().size());
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
        controller.createUser(user);
        assertEquals("login", controller.getUsers().get(0).getName());
    }

    @Test
    @DisplayName("Обновление валидного пользователя")
    void shouldBeUpdatedUser() {
        controller.createUser(user);
        User user1 = new User("email@yandex.ru", "login1", LocalDate.parse("1917-11-07"));
        user1.setId(1);
        System.out.println(controller.getUsers());
        controller.updateUser(user1);
        System.out.println(controller.getUsers());
        assertEquals("login1", controller.getUsers().get(0).getName());
    }

    @Test
    @DisplayName("Получение ошибки при обновлении пользователя, если такого не существует")
    void shouldGetExceptionWhenUpdatedUserWithWrongId() {
        controller.createUser(user);
        User user1 = new User("email@yandex.ru", "login1", LocalDate.parse("1917-11-07"));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.updateUser(user1));
        assertEquals("Такого пользователя не существует", exception.getMessage());
    }

    @Test
    @DisplayName("Получение списка пользователей")
    void shouldGetListOfFilms() {
        controller.createUser(user);
        assertEquals(1, controller.getUsers().size());
    }

    @Test
    @DisplayName("Получение пустого списка пользователей, если еще не создано ни одного пользователя")
    void shouldGetEmptyListOfFilms() {
        assertEquals(0, controller.getUsers().size());
    }
}