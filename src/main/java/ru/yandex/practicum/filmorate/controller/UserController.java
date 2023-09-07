package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final RecommendationService recommendationService;

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        log.info("Поступил запрос на создание пользователя");
        return userService.createUser(user);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        log.info(String.format("Поступил запрос на обновление пользователя с id %s", user.getId()));
        return userService.updateUser(user);
    }

    @GetMapping()
    public Collection<User> getUsers() {
        log.info("Поступил запрос на получение всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info(String.format("Поступил запрос на получение пользователя с id %s", id));
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Integer id) {
        log.info(String.format("Поступил запрос на удаление пользователя с id %s", id));
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable Integer id) {
        log.info(String.format("Поступил запрос на получение друзей пользователя с id %s", id));
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info(String.format("Поступил запрос на получение общих друзей пользователей с id %s и с id %s", id, otherId));
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addingToFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info(String.format("Поступил запрос на добавление пользователю с id %s друга с id %s", id, friendId));
        userService.addingToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deletingFromFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info(String.format("Поступил запрос на удаление у пользователя с id %s друга с id %s", id, friendId));
        userService.deletingFromFriends(id, friendId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable int id) {
        return recommendationService.getRecommendation(id);
    }

    @GetMapping("/{id}/feed")
    public List<UserEvent> getUserFeedById(@PathVariable Integer id) {
        log.info(String.format("Поступил запрос на получение действий пользователя с id %s", id));
        return userService.getUserEvents(id);
    }
}