package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
class RecomendationServiceTest {
    private final UserService userService;
    private final FilmService filmService;
    private final RecomendationService recomendationService;

    @BeforeEach
    void setUp() {
        List<User> userList = new ArrayList<>(10);
        List<Film> filmList = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            userService.createUser(new User("email" + i, "login" + i, "name" + i, LocalDate.of(1994, 10, 10)));
        }
        for (int i = 0; i <= 10; i++) {
            filmService.createFilm(new Film("name"+i,"description"+i,LocalDate.of(2005,5,23),60));

        }
        filmList = filmService.getFilms();
        for(Film film : filmList){
            int rnd = 10;
            int useInt =(int) (Math.random() * rnd)+1;

            for (int k = 0; k<= useInt; k++){
                film.setLike((int)(Math.random()* rnd)+1);
            }
        }
        for (Film film : filmList){
            filmService.updateFilm(film);
        }

    }

    @Test
    void testRecomendation(){
        recomendationService.getRecomendation(1);
    }
}