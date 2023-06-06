package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        Optional<User> first = userService.getUsers().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .findFirst();
        if (first.isPresent()) {
            throw new ValidationException("Пользователь с таким email уже существует");
        }
        return userService.createUser(user);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        Optional<User> first = userService.getUsers().stream()
                .filter(user1 -> user1.getId().equals(user.getId()))
                .findFirst();
        if (first.isEmpty()) {
            log.error("Пользователя с id {} не существует", user.getId());
            throw new NotFoundException(String.format("Пользователя с id %s не существует", user.getId()));
        }
        return userService.updateUser(user);
    }

    @GetMapping()
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        if (!userService.getUsers().contains(userService.getUserById(id))) {
            log.error("Пользователя с id {} не существует", id);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", id));
        }
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Integer id) {
        if (!userService.getUsers().contains(userService.getUserById(id))) {
            log.error("Пользователя с id {} не существует", id);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", id));
        }
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable Integer id) {
        if (!userService.getUsers().contains(userService.getUserById(id))) {
            log.error("Пользователя с id {} не существует", id);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", id));
        }
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        if (!userService.getUsers().contains(userService.getUserById(id))) {
            log.error("Пользователя с id {} не существует", id);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", id));
        }
        if (!userService.getUsers().contains(userService.getUserById(otherId))) {
            log.error("Пользователя с id {} не существует", otherId);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", otherId));
        }
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addingToFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (!userService.getUsers().contains(userService.getUserById(id))) {
            log.error("Пользователя с id {} не существует", id);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", id));
        }
        if (!userService.getUsers().contains(userService.getUserById(friendId))) {
            log.error("Пользователя с id {} не существует", friendId);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", friendId));
        }
        userService.addingToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deletingFromFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (!userService.getUsers().contains(userService.getUserById(id))) {
            log.error("Пользователя с id {} не существует", id);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", id));
        }
        if (!userService.getUsers().contains(userService.getUserById(friendId))) {
            log.error("Пользователя с id {} не существует", friendId);
            throw new NotFoundException(String.format("Пользователя с id %s не существует", friendId));
        }
        userService.deletingFromFriends(id, friendId);
    }
}