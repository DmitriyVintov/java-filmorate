package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private final UserController userController;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User("mail@mail.ru", "login", "name", LocalDate.parse("2011-01-01"));
        user2 = new User("mail2@mail.ru", "loginUpd", "nameUpd", LocalDate.parse("2011-01-01"));
        user3 = new User("mail3@mail.ru", "loginCommon", "nameCommon", LocalDate.parse("2011-01-01"));
    }

    @Test
    @DisplayName("Создание валидного пользователя")
    void createUser() {
        userController.createUser(user1);
        assertEquals(user1, userController.getUserById(1));
    }

    @Test
    @DisplayName("Обновление валидного пользователя")
    void updateUser() {
        userController.createUser(user1);
        user2.setId(1);
        userController.updateUser(user2);
        assertEquals(user2, userController.getUserById(1));
    }

    @Test
    @DisplayName("Получение списка пользователей")
    void getUsers() {
        userController.createUser(user1);
        userController.createUser(user2);
        assertEquals(2, userController.getUsers().size());
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getUserById() {
        User user1 = userController.createUser(this.user1);
        assertEquals(user1, userController.getUserById(1));
    }

    @Test
    @DisplayName("Удаление пользователя по id")
    void deleteUserById() {
        userController.createUser(user1);
        assertEquals(1, userController.getUsers().size());
        userController.deleteUserById(1);
        assertEquals(0, userController.getUsers().size());
    }

    @Test
    @DisplayName("Вывод списка друзей пользователя")
    void getFriends() {
        userController.createUser(user1);
        userController.createUser(user2);
        userController.addingToFriends(1, 2);
        assertEquals(1, userController.getFriends(1).size());
        assertEquals(0, userController.getFriends(2).size());
    }

    @Test
    @DisplayName("Вывод общих друзей двух пользователей")
    void getCommonFriends() {
        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);
        userController.addingToFriends(1, 3);
        userController.addingToFriends(2, 3);
        assertTrue(userController.getCommonFriends(1, 2).contains(user3));
        assertEquals(1, userController.getCommonFriends(1, 2).size());
    }

    @Test
    @DisplayName("Добавление друзей пользователю")
    void addingToFriends() {
        userController.createUser(user1);
        userController.createUser(user2);
        userController.addingToFriends(1, 2);
        assertTrue(userController.getFriends(1).contains(user2));
        assertEquals(1, userController.getFriends(1).size());
    }

    @Test
    @DisplayName("Удаление из друзей пользователя")
    void deletingFromFriends() {
        userController.createUser(user1);
        userController.createUser(user2);
        userController.addingToFriends(1, 2);
        assertTrue(userController.getFriends(1).contains(user2));
        assertEquals(1, userController.getFriends(1).size());
        userController.deletingFromFriends(1, 2);
        assertEquals(0, userController.getFriends(1).size());
    }
}