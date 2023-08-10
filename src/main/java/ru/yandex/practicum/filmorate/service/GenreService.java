package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final Storage<Genre> genreStorage;

    public GenreService(Storage<Genre> genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getGenres() {
        log.info("Все жанры: {}", genreStorage.getAll());
        return genreStorage.getAll();
    }

    public Genre getGenreById(Integer id) {
        if (!getGenres().contains(genreStorage.getById(id))) {
            log.error("Жанра с id {} не существует", id);
            throw new NotFoundException(String.format("Жанра с id %s не существует", id));
        }
        log.info("Получение жанра с id {}: {}", id, genreStorage.getById(id));
        return genreStorage.getById(id);
    }
}