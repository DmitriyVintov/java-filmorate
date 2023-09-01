package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RecommendationService {

    private final FilmService filmService;
    private final UserService userService;

    public List<Film> getRecommendation(int userId) {
        List<User> users = userService.getUsers();
        List<Film> allFilm = filmService.getFilms();
        Map<User, Map<Film, Integer>> userFilmsHashMap = preferencesUsersMapGenerate(users, allFilm);
        Map<User, Integer> userIntegerHashMapIntersect = intersectPreferencesUsersMapGenerate(users, userFilmsHashMap);
        List<User> maxIntersect = getListUsersWithMaxIntersect(userIntegerHashMapIntersect);

        return createRecommendationsFoUserById(userId, maxIntersect, userFilmsHashMap);
    }

    private List<Film> createRecommendationsFoUserById(int userId, List<User> maxIntersect,
                                                       Map<User, Map<Film, Integer>> userFilmsHashMap) {
        User user1 = userService.getById(userId);
        List<Film> recomendationList = new ArrayList<>();
        for (User user2 : maxIntersect) {
            if (!Objects.equals(user1, user2)) {
                Map<Film, Integer> filmsUser1 = userFilmsHashMap.get(user1);
                Map<Film, Integer> filmsUser2 = userFilmsHashMap.get(user2);

                for (Map.Entry<Film, Integer> entry : filmsUser1.entrySet()) {
                    for (Map.Entry<Film, Integer> entry1 : filmsUser2.entrySet()) {
                        if (Objects.equals(entry.getKey(), entry1.getKey()) && entry.getValue() == 0
                                && entry1.getValue() == 1 && !recomendationList.contains(entry.getKey())) {
                            recomendationList.add(entry.getKey());
                        }
                    }
                }
            }
        }

        return recomendationList;
    }

    private List<User> getListUsersWithMaxIntersect(Map<User, Integer> mapIntersect) {
        SortedSet<Map.Entry<User, Integer>> sortedIntersect = entriesSortedByValues(mapIntersect);
        int maxCountIntersect = 0;
        List<User> usersListWithMaxIntersect = new ArrayList<>();

        for (Map.Entry<User, Integer> ent : sortedIntersect) {
            if (maxCountIntersect <= ent.getValue()) {
                maxCountIntersect = ent.getValue();
                usersListWithMaxIntersect.add(ent.getKey());
            }
        }

        return usersListWithMaxIntersect;
    }

    private Map<User, Integer> intersectPreferencesUsersMapGenerate(List<User> users,
                                                                    Map<User, Map<Film, Integer>> userFilmsHashMap) {
        Map<User, Integer> userIntegerHashMapIntersect = new HashMap<>();

        for (User user1 : users) {
            for (User user2 : users) {
                if (!Objects.equals(user1, user2)) {
                    Map<Film, Integer> filmsUser1 = userFilmsHashMap.get(user1);
                    Map<Film, Integer> filmsUser2 = userFilmsHashMap.get(user2);
                    List<Film> intersect = new ArrayList<>();

                    for (Film film : filmsUser1.keySet()) {
                        if (filmsUser1.get(film) == 1 & filmsUser2.get(film) == 1) {
                            intersect.add(film);
                        }
                    }
                    if (userIntegerHashMapIntersect.getOrDefault(user1, 0) < intersect.size())
                        userIntegerHashMapIntersect.put(user1, intersect.size());
                }
            }
        }
        return userIntegerHashMapIntersect;
    }

    private Map<User, Map<Film, Integer>> preferencesUsersMapGenerate(List<User> users, List<Film> allFilm) {
        Map<User, Map<Film, Integer>> userFilmsHashMap = new HashMap<>();
        for (User user : users) {
            Map<Film, Integer> likesFilmMap = new HashMap<>();
            List<Film> userLikesFilms = filmService.getFilms().stream().filter(e -> e.getLikes()
                    .contains(user.getId())).collect(Collectors.toList());

            for (Film film : allFilm) {
                if (userLikesFilms.contains(film)) {
                    likesFilmMap.put(film, 1);
                } else {
                    likesFilmMap.put(film, 0);
                }
            }
            userFilmsHashMap.put(user, likesFilmMap);
        }

        return userFilmsHashMap;
    }

    private <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                (e1,e2) -> {
                        int res = e2.getValue().compareTo(e1.getValue());
                        return res != 0 ? res : 1;
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
