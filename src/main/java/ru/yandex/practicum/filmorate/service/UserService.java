package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        User newUser = userStorage.create(user);
        log.info("Создан пользователь: {}", newUser.toString());
        return newUser;
    }

    public List<User> getUsers() {
        log.info("Получение всех пользователей");
        return userStorage.get();
    }

    public User getUserById(Integer id) {
        log.info("Получение пользователя с id {}", id);
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
        return getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        log.info("Получение общих друзей у пользователей с id {} и {}", id, otherId);
        Set<Integer> friendsByOtherId = getUserById(otherId).getFriends();
        return getUserById(id).getFriends().stream()
                .filter(friendsByOtherId::contains)
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }

    public void addingToFriends(Integer id, Integer friendId) {
        log.info("Добавление в друзья пользователю с id {} пользователя с id {}", id, friendId);
        userStorage.getById(id).setFriends(friendId);
        userStorage.getById(friendId).setFriends(id);
    }

    public void deletingFromFriends(Integer id, Integer friendId) {
        log.info("Удаление из друзей пользователя с id {} пользователя с id {}", id, friendId);
        userStorage.getById(id).removeFriends(friendId);
        userStorage.getById(friendId).removeFriends(id);
    }
}