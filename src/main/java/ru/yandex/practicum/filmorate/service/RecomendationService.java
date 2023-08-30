package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class RecomendationService {
    private FilmService filmService;
    private UserService userService;
    private GenreService genreService;

    public void getRecomendation(int userId) {
        Map<User, HashMap<Film, Integer>> userFilmsHashMap = new HashMap<>();
        Map<User, HashMap<Film, Integer>> diff = new HashMap<>();
        Map<User, HashMap<Film, Integer>> freq = new HashMap<>();
        Map<Film, Integer> filmIntegerHashMap = new HashMap<>();

        filmService.getFilms().stream().map(e -> e.getLikes().stream().map(e -> userFilmsHashMap.put(userFilmsHashMap.getOrDefault() userService.getById(e),))).close();

        for (User user : userService.getUsers()){

            filmIntegerHashMap.put()
            userFilmsHashMap.put(user,)
        }

        List<Film> popular =  filmService.getMostPopularFilms(10);
        Map<Film,Double> raitingFilm = new HashMap<>();
        raitingFilm.put(popular.get(0),1.5);
        raitingFilm.put(popular.get(popular.size()/2),1.0);
        raitingFilm.put(popular.get(popular.size()-1),0.0);

        filmService.getFilms().stream().filter(e -> e.getLikes().contains(userId)).peek(e -> e.getGenres().stream()
                .peek(g -> genreIntegerMap.put(g, genreIntegerMap.getOrDefault(g, 0) + 1)).close()).close();

//        for(Film film : filmService.getFilms()) {
//            if(film.getLikes().contains(userId)){
//                for(Genre genre : film.getGenres()) {
//                    genreIntegerMap.put(genre, genreIntegerMap.getOrDefault(genre, 0) + 1);
//                }
//            }
//        }
//Оценка выставляется на основе того сколько лайков у фильма а что если нам посчитать фильм с максимальным количеством
// лайков(задесь может пригодиться топ фильмов) это будет 1,5 балла затем со среднем 1 балл и с низким это будет 0 баллов
// это мы определим степень популярности фильмов у пользователей, на основе этого рейтинга у нас будет понимание что
// нравиться массам и выдавать это пользователю

        for (HashMap<Film, Integer> films : userFilmsHashMap.values()) {
            for (Map.Entry<Genre, Integer> e : films.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<>());
                    freq.put(e.getKey(), new HashMap<>());
                }


                for (Map.Entry<Genre, Integer> e2 : films.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
                    }

                    int oldDiff = 0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey()).intValue();
                    }

                    int observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
                for (Film j : diff.keySet()) {
                    for (Film i : diff.get(j).keySet()) {
                        double oldValue = diff.get(j).get(i).doubleValue();
                        int count = freq.get(j).get(i).intValue();
                        diff.get(j).put(i, (int) (oldValue / count));
                    }
                }
            }
            System.out.println(diff);
        }
    }
}
