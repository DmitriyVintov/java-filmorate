package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final Storage<Director> directorStorage;


    public List<Director> getAll() {
        return directorStorage.getAll();
    }


    public Director getById(Integer id) throws NotFoundException {
        return directorStorage.getById(id);
    }


    public Director create(Director director) {
        return directorStorage.create(director);
    }


    public Director update(Director director) {
        return directorStorage.update(director);
    }


    public void deleteById(Integer id) {
        directorStorage.deleteById(id);
    }

}
