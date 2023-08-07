package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        log.info("Все пользователи: {}", userStorage.getAll());
        return userStorage.getAll();
    }

    public User getById(Integer id) {
        log.info("Получение пользователя с id {}: {}", id, userStorage.getById(id));
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
        log.info("Список друзей пользователя id {}: {}", id, userStorage.getById(id).getFriends().toString());
        return userStorage.getById(id).getFriends().stream()
                .map(this::getById)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        Set<Integer> friendsByOtherId = getById(otherId).getFriends();
        log.info("Получение общих друзей у пользователей с id {} и {}: {}", id, otherId, getById(id).getFriends().stream()
                .filter(friendsByOtherId::contains)
                .map(this::getById)
                .collect(Collectors.toSet()));
        return getById(id).getFriends().stream()
                .filter(friendsByOtherId::contains)
                .map(this::getById)
                .collect(Collectors.toSet());
    }

    public void addingToFriends(Integer id, Integer friendId) {
        log.info("Добавление в друзья пользователю с id {} пользователя с id {}", id, friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deletingFromFriends(Integer id, Integer friendId) {
        log.info("Удаление из друзей пользователя с id {} пользователя с id {}", id, friendId);
        userStorage.deleteFriend(id, friendId);
    }
}