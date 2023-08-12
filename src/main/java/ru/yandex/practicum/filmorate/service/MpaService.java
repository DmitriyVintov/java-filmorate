package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final Storage<Mpa> mpaStorage;

    public MpaService(Storage<Mpa> mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getMpaList() {
        List<Mpa> allMpa = mpaStorage.getAll();
        log.info("Все рейтинги MPA: {}", allMpa);
        return allMpa;
    }

    public Mpa getMpaById(Integer id) {
        Mpa mpaById = mpaStorage.getById(id);
        if (mpaById == null) {
            log.error("Рейтинга MPA с id {} не существует", id);
            throw new NotFoundException(String.format("Рейтинга MPA с id %s не существует", id));
        }
        log.info("Получение рейтинга MPA с id {}: {}", id, mpaById);
        return mpaById;
    }
}
