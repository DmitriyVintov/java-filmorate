package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
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
        validateOnCreateUser(user);
        log.info("Создан пользователь: {}", user.toString());
        return userStorage.create(user);
    }

    public List<User> getUsers() {
        log.info("Все пользователи: {}", userStorage.getAll());
        return userStorage.getAll();
    }

    public User getById(Integer id) {
        validateUser(id);
        log.info("Получение пользователя с id {}: {}", id, userStorage.getById(id));
        return userStorage.getById(id);
    }

    public User updateUser(User user) {
        validateUser(user.getId());
        log.info("Обновлен пользователь: {}", user.toString());
        return userStorage.update(user);
    }

    public void deleteUserById(Integer id) {
        validateUser(id);
        userStorage.deleteById(id);
        log.info("Пользователь с id {} удален", id);
    }

    public Set<User> getFriends(Integer id) {
        validateUser(id);
        log.info("Список друзей пользователя id {}: {}", id, userStorage.getById(id).getFriends().toString());
        return userStorage.getById(id).getFriends().stream()
                .map(this::getById)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        validateUser(id);
        validateUser(otherId);
        validateSelfAddition(id, otherId);
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
        validateUser(id);
        validateUser(friendId);
        validateSelfAddition(id, friendId);
        log.info("Добавление в друзья пользователю с id {} пользователя с id {}", id, friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deletingFromFriends(Integer id, Integer friendId) {
        validateUser(id);
        validateUser(friendId);
        validateSelfAddition(id, friendId);
        log.info("Удаление из друзей пользователя с id {} пользователя с id {}", id, friendId);
        userStorage.deleteFriend(id, friendId);
    }

    private void validateOnCreateUser(User user) {
        Optional<User> userValidationEmail = getUsers().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .findFirst();
        if (userValidationEmail.isPresent()) {
            throw new DataAlreadyExistException("Пользователь с таким email уже существует");
        }
        Optional<User> userValidationLogin = getUsers().stream()
                .filter(user1 -> user1.getLogin().equals(user.getLogin()))
                .findFirst();
        if (userValidationLogin.isPresent()) {
            throw new DataAlreadyExistException("Пользователь с таким login уже существует");
        }
        Optional<User> userValidationName = getUsers().stream()
                .filter(user1 -> user1.getName().equals(user.getName()))
                .findFirst();
        if (userValidationName.isPresent()) {
            throw new DataAlreadyExistException("Пользователь с таким именем уже существует");
        }
    }

    private void validateUser(Integer userId) {
        if (!userStorage.getAll().contains(userStorage.getById(userId))) {
            log.error("Пользователя с id {} не существует", userId);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", userId));
        }
    }

    private void validateSelfAddition(Integer userId, Integer friendId) {
        if (Objects.equals(userId, friendId)) {
            log.error("Невозможно добавить себя в друзья или удалить из друзей");
            throw new ValidationException("Невозможно добавить себя в друзья или удалить из друзей");
        }
    }
}