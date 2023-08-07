package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;

public interface Storage<T> {

    List<T> getAll();

    T getById(Integer id) throws NotFoundException;

    T create(T t);

    T update(T t);

    void deleteById(Integer id);
}