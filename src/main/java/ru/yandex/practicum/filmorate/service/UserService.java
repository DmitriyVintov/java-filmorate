package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.model.UserEventOperation;
import ru.yandex.practicum.filmorate.model.UserEventType;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.DbUserEventStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("dbUserStorage")
@Slf4j
public class UserService {
    @Qualifier("dbUserStorage")
    private final UserStorage userStorage;
    private final DbUserEventStorage eventStorage;

    public UserService(@Qualifier("dbUserStorage") UserStorage userStorage, DbUserEventStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public User createUser(User user) {
        validateOnCreateUser(user);
        log.info("Создан пользователь: {}", user.toString());
        return userStorage.create(user);
    }

    public List<User> getUsers() {
        List<User> allUsers = userStorage.getAll();
        log.info("Все пользователи: {}", allUsers);
        return allUsers;
    }

    public User getById(Integer id) {
        validateUser(id);
        User userById = userStorage.getById(id);
        log.info("Получение пользователя с id {}: {}", id, userById);
        return userById;
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
        Set<Integer> friends = userStorage.getById(id).getFriends();
        log.info("Список друзей пользователя id {}: {}", id, friends.toString());
        return friends.stream()
                .map(this::getById)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<User> getCommonFriends(Integer id, Integer otherId) {
        validateUser(id);
        validateUser(otherId);
        validateSelfAddition(id, otherId);
        Set<Integer> friendsByOtherId = getById(otherId).getFriends();
        Set<User> commonFriends = getById(id).getFriends().stream()
                .filter(friendsByOtherId::contains)
                .map(this::getById)
                .collect(Collectors.toSet());
        log.info("Получение общих друзей у пользователей с id {} и {}: {}", id, otherId, commonFriends);
        return commonFriends;
    }

    public void addingToFriends(Integer id, Integer friendId) {
        validateUser(id);
        validateUser(friendId);
        validateSelfAddition(id, friendId);
        log.info("Добавление в друзья пользователю с id {} пользователя с id {}", id, friendId);
        eventStorage.create(new UserEvent(id, UserEventType.FRIEND, UserEventOperation.ADD, getById(friendId).getId()));
        userStorage.addFriend(id, friendId);
    }

    public void deletingFromFriends(Integer id, Integer friendId) {
        validateUser(id);
        validateUser(friendId);
        validateSelfAddition(id, friendId);
        log.info("Удаление из друзей пользователя с id {} пользователя с id {}", id, friendId);
        eventStorage.create(new UserEvent(id, UserEventType.FRIEND, UserEventOperation.REMOVE, getById(friendId).getId()));
        userStorage.deleteFriend(id, friendId);
    }

    public List<UserEvent> getUserEvents(Integer userId) {
        validateUser(userId);
        log.info("Получение событий пользователя с id {}: {}", userId, eventStorage.getAll(userId));
        return eventStorage.getAll(userId);
    }

    private void validateOnCreateUser(User user) {
        List<User> users = getUsers();
        Optional<User> userValidationEmail = users.stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .findFirst();
        if (userValidationEmail.isPresent()) {
            throw new DataAlreadyExistException("Пользователь с таким email уже существует");
        }
        Optional<User> userValidationLogin = users.stream()
                .filter(user1 -> user1.getLogin().equals(user.getLogin()))
                .findFirst();
        if (userValidationLogin.isPresent()) {
            throw new DataAlreadyExistException("Пользователь с таким login уже существует");
        }
        Optional<User> userValidationName = users.stream()
                .filter(user1 -> user1.getName().equals(user.getName()))
                .findFirst();
        if (userValidationName.isPresent()) {
            throw new DataAlreadyExistException("Пользователь с таким именем уже существует");
        }
    }

    private void validateUser(Integer userId) {
        if (userStorage.getById(userId) == null) {
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