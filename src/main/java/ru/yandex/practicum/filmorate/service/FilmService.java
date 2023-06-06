package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public Film createFilm(Film film) {
        Film newFilm = filmStorage.createFilm(film);
        log.info("Создан фильм: {}", film.toString());
        return newFilm;
    }

    public List<Film> getFilms() {
        log.info("Получение всех фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        log.info("Получение фильма с id {}", id);
        return filmStorage.getFilmById(id);
    }

    public Film updateFilm(Film film) {
        log.info("Фильм обновлен: {}", film.toString());
        return filmStorage.updateFilm(film);
    }

    public void deleteFilmById(Integer id) {
        log.info("Фильм с id {} удален", id);
        filmStorage.deleteFilmById(id);
    }

    public void setLike(Integer filmId, Integer userId) {
        log.info("Пользователь id {} поставил лайк фильму id {}", userId, filmId);
        filmStorage.getFilmById(filmId).setLikes(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        log.info("Пользователь id {} удалил лайк у фильма id {}", userId, filmId);
        filmStorage.getFilmById(filmId).removeLike(userId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        log.info("Получение списка самых популярных фильмов");
        return filmStorage.getFilms().stream()
                .sorted(Comparator.nullsLast(Comparator.comparingInt((Film film) -> film.getLikes().size()))
                        .thenComparing(Film::getReleaseDate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}