package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@Service
@Qualifier("dbUserStorage")
@Slf4j
public class UserService {
    @Qualifier("dbUserStorage")
    private final UserStorage userStorage;

    public UserService(@Qualifier("dbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        User newUser = userStorage.create(user);
        log.info("Создан пользователь: {}", newUser.toString());
        return newUser;
    }

    public List<User> getUsers() {
//        log.info("Получение всех пользователей");
        return userStorage.get();
    }

    public User getUserById(Integer id) {
//        log.info("Получение пользователя с id {}", id);
        return userStorage.getById(id);
    }

    public User updateUser(User user) {
        log.info("Обновлен пользователь: {}", user.toString());
        return userStorage.update(user);
    }

    public void deleteUserById(Integer id) {
        userStorage.deleteById(id);
        log.info("Пользователь с id {} удален", id);
    }

    public Set<User> getFriends(Integer id) {
        log.info("Получение друзей у пользователя с id {}", id);
        log.info("Список друзей пользователя id {}: {}", id, userStorage.getFriends(id).toString());
        return userStorage.getFriends(id);
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        log.info("Получение общих друзей у пользователей с id {} и {}", id, otherId);
        log.info("Список общих друзей: {}", userStorage.getCommonFriends(id, otherId).toString());
        return userStorage.getCommonFriends(id, otherId);
    }

    public void addingToFriends(Integer id, Integer friendId) {
        log.info("Добавление в друзья пользователю с id {} пользователя с id {}", id, friendId);
        userStorage.addingToFriends(id, friendId);
    }

    public void deletingFromFriends(Integer id, Integer friendId) {
        log.info("Удаление из друзей пользователя с id {} пользователя с id {}", id, friendId);
        userStorage.deletingFromFriends(id, friendId);
    }
}