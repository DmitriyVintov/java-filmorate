package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;

public interface StorageAdv<T> extends Storage<T> {

    List<T> get();

    T getById(Integer id) throws NotFoundException;

    T create(T t);

    T update(T t);

    void deleteById(Integer id);
}