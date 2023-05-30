package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(id);
        log.info("Пользователь с id {} создан", id);
        users.put(id, user);
        id++;
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Такого пользователя не существует");
            throw new ValidationException("Такого пользователя не существует");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Пользователь с id {} обновлен", id);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping()
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}

